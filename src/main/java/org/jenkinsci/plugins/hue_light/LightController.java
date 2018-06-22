package org.jenkinsci.plugins.hue_light;


import nl.q42.jue.HueBridge;
import nl.q42.jue.Light;
import nl.q42.jue.State;
import nl.q42.jue.StateUpdate;

import java.io.PrintStream;
import java.util.HashSet;

public class LightController {
    private final PrintStream logger;
    private HashSet<HueBridge> hueBridges;
    private int saturation;
    private int brightness;

    /**
     * Connect with a hue bridge.
     *
     * @param descriptor The descriptor for this application
     * @param logger     logger stream
     */
    public LightController(LightNotifier.DescriptorImpl descriptor, PrintStream logger) {
        this.logger = logger;
        this.saturation = Integer.parseInt(descriptor.getSaturation());
        this.brightness = Integer.parseInt(descriptor.getBrightness());

        try {
            String[] bridgeIps = descriptor.getBridgeIp().split(",");
            String[] userNames = descriptor.getBridgeUsername().split(",");
            this.hueBridges = new HashSet<HueBridge>();
            for(int i =0;i<bridgeIps.length;i++) {
                // 此处捕获异常，防止某个HueBridge连不上会影响后面的HueBridge初始化
                try {
                    this.hueBridges.add(new HueBridge(bridgeIps[i], userNames[i]));
                } catch (Exception e) {
                    this.logError(bridgeIps[i]);
                    this.logError(e);
                }
            }
        } catch (Exception e) {
            this.logError(e);
        }
    }
    
    public HueBridge getHueBridgeByIp(String ip) {
        if(null != this.hueBridges) {
            for(HueBridge hueBridge : hueBridges) {
                if(ip.equalsIgnoreCase(hueBridge.getIPAddress())) {
                    return hueBridge;
                }
            }
        }
        return null;
    }
    
    public HueBridge getHueBridgeById(String id) {
        String ip = id.split("&")[0];
        return this.getHueBridgeByIp(ip);
    }

    /**
     * Returns a light object for a specific id.
     *
     * @param id id of a light, bridgeIp&lightId,eg:192.168.1.108&1
     * @return light object if light found, otherwise null
     */
    public Light getLightForId(String id) {
        String ip = id.split("&")[0];
        String lightId = id.split("&")[1];
        HueBridge hueBridge = getHueBridgeByIp(ip);
        if (null != hueBridge) {
            try {
                for (Light light : hueBridge.getLights()) {
                    if (light.getId().equals(lightId)) {
                        return light;
                    }
                }
            } catch (Exception e) {
                this.logError(e);
            }
        }

        return null;
    }

    /**
     * Sets the color of a light.
     *
     * @param light light object that should be manipulated
     * @param logName The name to use for logging this color state change
     * @param hue The hue of the desired color
     * @return true if color update was successful, otherwise false
     */
    public boolean setColor(HueBridge hueBridge, Light light, String logName, int hue) {

        if (null == hueBridge || null == light)
            return false;

        StateUpdate stateUpdate = new StateUpdate().turnOn().setBrightness(this.brightness).setSat(this.saturation).setHue(hue).setEffect(State.Effect.NONE).setAlert((State.AlertMode.NONE));

        try {
            hueBridge.setLightState(light, stateUpdate);
            this.logInfo("set color of light " + light.getId() + " (" + light.getName() + ")" + " to " + logName + " (" + hue + ")");
        } catch (Exception e) {
            this.logError(e);
            return false;
        }

        return true;
    }

    public boolean setPulseColor(HueBridge hueBridge, Light light, String logName, int hue) {

        if (null == hueBridge || null == light)
            return false;

        StateUpdate stateUpdate = new StateUpdate().turnOn().setBrightness(this.brightness).setSat(this.saturation).setHue(hue).setEffect(State.Effect.COLORLOOP).setAlert(State.AlertMode.NONE);

        try {
            hueBridge.setLightState(light, stateUpdate);
            this.logInfo("set pulse color of light " + light.getId() + " (" + light.getName() + ")" + " to " + logName + " (" + hue + ")");
        } catch (Exception e) {
            this.logError(e);
            return false;
        }

        return true;
    }

    public boolean setPulseBreathe(HueBridge hueBridge, Light light, String logName, int hue) {

        if (null == hueBridge || null == light)
            return false;

        StateUpdate stateUpdate = new StateUpdate().turnOn().setBrightness(this.brightness).setSat(this.saturation).setHue(hue).setEffect(State.Effect.NONE).setAlert(State.AlertMode.LSELECT);

        try {
            hueBridge.setLightState(light, stateUpdate);
            this.logInfo("set breathe color of light " + light.getId() + " (" + light.getName() + ")" + " to " + logName + " (" + hue + ")");
        } catch (Exception e) {
            this.logError(e);
            return false;
        }

        return true;
    }
    
    private void logError(String msg) {
        this.logger.println("zmr hue-light-error: " + msg);
    }

    private void logError(Exception e) {
        e.printStackTrace(logger);
    }
    
    private void logInfo(String msg) {
        this.logger.println("zmr hue-light: " + msg);
    }
}
