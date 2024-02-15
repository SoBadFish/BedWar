package org.sobadfish.bedwar.room.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sobadfish
 *  2023/2/20
 */
public class KnockConfig {

    public boolean enable = true;

    public float force = 0.4f;

    public float speed = 0.5f;

    public float motionY = 0.1f;



    public Map<String, Object> saveConfig(){
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("enable",enable);
        config.put("force",force);
        config.put("speed",speed);
        config.put("motionY",motionY);
        return config;
    }
}
