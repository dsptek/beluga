package org.opencloudengine.garuda.beluga.cloud;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.opencloudengine.garuda.beluga.env.ClusterPorts;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.exception.InvalidRoleException;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
* 현재 생성되어 운영중인 클러스터의 형상을 담고있다.
* Created by swsong on 2015. 7. 20..
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterTopology {
    private static final String REGISTRY_ADDRESS_PORT_FORMAT = "%s:" + ClusterPorts.REGISTRY_PORT;
    private static final String REGISTRY_ENDPOINT_FORMAT = "http://%s:" + ClusterPorts.REGISTRY_PORT;
    private static final String MESOS_ENDPOINT_FORMAT = "http://%s:" + ClusterPorts.MESOS_PORT;
    private static final String MARATHON_ENDPOINT_FORMAT = "http://%s:" + ClusterPorts.MARATHON_PORT;

    public static final String IAAS_PROFILE_KEY = "iaasProfile";
    public static final String DEFINITION_ID_KEY = "definitionId";
    public static final String CLUSTER_ID_KEY = "clusterId";

    public static final String PROXY_ROLE = "proxy";
    public static final String MESOS_MASTER_ROLE = "mesos-master";
    public static final String MESOS_SLAVE_ROLE = "mesos-slave";
    public static final String MANAGEMENT_DB_REGISTRY_ROLE = "management";

    public static final String ID_SUFFIX = ".id";
    public static final String IP_SUFFIX = ".ip";

    private String clusterId;
    private String definitionId;
    private String iaasProfile;

    private List<CommonInstance> proxyList;
    private List<CommonInstance> mesosMasterList;
    private List<CommonInstance> mesosSlaveList;
    private List<CommonInstance> managementList;
    private List<CommonInstance> noRoleNodeList;


    public ClusterTopology(String clusterId, String definitionId, String iaasProfile) {
        this.clusterId = clusterId;
        this.definitionId = definitionId;
        this.iaasProfile = iaasProfile;

        proxyList = new ArrayList<>();
        mesosMasterList = new ArrayList<>();
        mesosSlaveList = new ArrayList<>();
        managementList = new ArrayList<>();
        noRoleNodeList = new ArrayList<>();
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public String getIaasProfile() {
        return iaasProfile;
    }

    @XmlTransient
    public List<CommonInstance> getAllNodeList() {
        List<CommonInstance> list = new ArrayList<>();
        list.addAll(proxyList);
        list.addAll(mesosMasterList);
        list.addAll(mesosSlaveList);
        list.addAll(managementList);
        list.addAll(noRoleNodeList);
        return list;
    }

    public List<CommonInstance> getProxyList() {
        return proxyList;
    }

    public List<CommonInstance> getMesosMasterList() {
        return mesosMasterList;
    }

    public List<CommonInstance> getMesosSlaveList() {
        return mesosSlaveList;
    }

    public List<CommonInstance> getManagementList() {
        return managementList;
    }

    public List<CommonInstance> getInstancesByRole(String role) throws InvalidRoleException {
        switch (role) {
            case PROXY_ROLE:
                return proxyList;
            case MESOS_MASTER_ROLE:
                return mesosMasterList;
            case MESOS_SLAVE_ROLE:
                return mesosSlaveList;
            case MANAGEMENT_DB_REGISTRY_ROLE:
                return managementList;
            default:
                throw new InvalidRoleException("no such role : " + role);
        }
    }

    public void addNode(String role, CommonInstance commonInstance) throws InvalidRoleException {
        switch (role) {
            case PROXY_ROLE:
                proxyList.add(commonInstance);
                break;
            case MESOS_MASTER_ROLE:
                mesosMasterList.add(commonInstance);
                break;
            case MESOS_SLAVE_ROLE:
                mesosSlaveList.add(commonInstance);
                break;
            case MANAGEMENT_DB_REGISTRY_ROLE:
                managementList.add(commonInstance);
                break;
            default:
                noRoleNodeList.add(commonInstance);
                throw new InvalidRoleException("no such role : " + role);
        }
    }

    public void loadRoles(Settings settings, Iaas iaas) throws InvalidRoleException {
        loadRole(ClusterTopology.PROXY_ROLE, settings, iaas);
        loadRole(ClusterTopology.MESOS_MASTER_ROLE, settings, iaas);
        loadRole(ClusterTopology.MESOS_SLAVE_ROLE, settings, iaas);
        loadRole(ClusterTopology.MANAGEMENT_DB_REGISTRY_ROLE, settings, iaas);
    }
    private void loadRole(String role, Settings settings, Iaas iaas) throws InvalidRoleException {
        String value = settings.getValue(role + ID_SUFFIX);
        if(value != null && value.trim().length() > 0) {
            String[] idArray = settings.getStringArray(role + ID_SUFFIX);
            List<String> idList = new ArrayList<String>();
            for(String id : idArray) {
                idList.add(id);
            }
            List<CommonInstance> list = iaas.getInstances(idList);
            for(CommonInstance instance : list) {
                addNode(role, instance);
            }
        }
    }

    @Override
    public String toString() {
        return getProperties().toString();
    }

    @XmlTransient
    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty(CLUSTER_ID_KEY, clusterId);
        props.setProperty(DEFINITION_ID_KEY, definitionId);
        props.setProperty(IAAS_PROFILE_KEY, iaasProfile);
        putProps(props, PROXY_ROLE, proxyList);
        putProps(props, MESOS_MASTER_ROLE, mesosMasterList);
        putProps(props, MESOS_SLAVE_ROLE, mesosSlaveList);
        putProps(props, MANAGEMENT_DB_REGISTRY_ROLE, managementList);
        return props;
    }

    private void putProps(Properties props, String roleName, List<CommonInstance> nodeList) {
        StringBuffer idList = new StringBuffer();
        StringBuffer ipAddressList = new StringBuffer();
        for(CommonInstance d : nodeList) {
            String id = d.getInstanceId();
            String ip = d.getPublicIpAddress();
            if(idList.length() > 0) {
                idList.append(",");
            }
            if(ipAddressList.length() > 0) {
                ipAddressList.append(",");
            }
            idList.append(id);
            ipAddressList.append(ip);
        }
        props.put(roleName + ID_SUFFIX, idList.toString());
        props.put(roleName + IP_SUFFIX, ipAddressList.toString());
    }

    public String getRegistryAddressPort() {
        if(managementList.size() > 0) {
            //무조건 1개로 처리.
            String ipAddress = managementList.get(0).getPublicIpAddress();
            return String.format(REGISTRY_ADDRESS_PORT_FORMAT, ipAddress);
        }
        return null;
    }
    public String getRegistryEndPoint() {
        if(managementList.size() > 0) {
            //무조건 1개로 처리.
            String ipAddress = managementList.get(0).getPublicIpAddress();
            return String.format(REGISTRY_ENDPOINT_FORMAT, ipAddress);
        }
        return null;
    }

    public List<String> getMarathonEndPoints() {
        List<String> list = new ArrayList<>();
        for(CommonInstance i : mesosMasterList) {
            list.add(String.format(MARATHON_ENDPOINT_FORMAT, i.getPublicIpAddress()));
        }
        return list;
    }

    public List<String> getMesosMasterEndPoints() {
        List<String> list = new ArrayList<>();
        for(CommonInstance i : mesosMasterList) {
            list.add(String.format(MESOS_ENDPOINT_FORMAT, i.getPublicIpAddress()));
        }
        return list;
    }
}
