package org.opencloudengine.garuda.beluga.cloud;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 26..
 */
public class IaasSpec {

    private int cpu;
    private int memory;
    private int disk;

    public static final String EC2_TYPE = "ec2";
    public static final String OPENSTACK_TYPE = "openstack";

    private static Map<String, Map<String, IaasSpec>> iaasSpecMap = new HashMap<>();
    private static Map<String, IaasSpec> awsMap = new HashMap<>();
    private static Map<String, IaasSpec> openstackMap = new HashMap<>();
    static {
        iaasSpecMap.put(EC2_TYPE, awsMap);
        iaasSpecMap.put(OPENSTACK_TYPE, openstackMap);

        awsMap.put("t2.micro", new IaasSpec(1, 1, 0));
        awsMap.put("t2.small", new IaasSpec(1, 2, 0));
        awsMap.put("t2.medium", new IaasSpec(2, 4, 0));
        awsMap.put("t2.large", new IaasSpec(2, 8, 0));
    }
    public static IaasSpec getSpec(String iaasType, String instanceType) {
        Map<String, IaasSpec> map = iaasSpecMap.get(iaasType);
        if(map == null) {
            return null;
        }
        return map.get(instanceType);
    }

    public IaasSpec(int cpu, int memory, int disk) {
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }

    public int getCpu() {
        return cpu;
    }

    public int getMemory() {
        return memory;
    }

    public int getDisk() {
        return disk;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " cpu[" + cpu + "] memory[" + memory + " GB] disk[" + disk + " GB]";
    }
}