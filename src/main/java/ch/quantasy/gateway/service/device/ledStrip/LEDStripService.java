/*
 * Within this step, a service is using the MQTTCommunication and the 'business-logic'
 * The response of the 'business logic' is promoted to the event topic.
 * The 'business-logic' is invoked via the service and then is self-sustaining.
 * The 'business-logic' sends the result via Callback
 * This way we have a Model-'View'-Presenter (MVP) Where the presenter (the service) is glueing together
 * the Model ('business-logic') and the 'View' (the MQTT-Communication)
 * The service is promoting more status information to the status topic about the underlying 'business-logic'.
 *
 * This time, an agent is communicating with the service and controls it.
 * This way we delve into the Service based Agent oriented programming
 */
package ch.quantasy.gateway.service.device.ledStrip;

import ch.quantasy.gateway.service.device.AbstractDeviceService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ch.quantasy.tinkerforge.device.led.LEDStripDevice;
import ch.quantasy.tinkerforge.device.led.LEDStripDeviceCallback;
import ch.quantasy.tinkerforge.device.led.LEDStripDeviceConfig;

/**
 *
 * @author reto
 */
public class LEDStripService extends AbstractDeviceService<LEDStripDevice, LEDStripServiceContract> implements LEDStripDeviceCallback {

    private short[][] leds;

    public LEDStripService(LEDStripDevice device) throws MqttException {
        super(device, new LEDStripServiceContract(device));
        addDescription(getServiceContract().INTENT_CONFIG, "chipType: [WS2801|WS2811|WS2812]\n frameDurationInMilliseconds: [0.." + Long.MAX_VALUE + "]\n clockFrequencyOfICsInHz: [10000..2000000]\n numberOfLEDs: [1..320]\n channelMapping: [rgb|rbg|grb|gbr|brg|bgr]");
        addDescription(getServiceContract().INTENT_LEDs, "[{{r,r,...,r}_numLEDs {g,g,...,g}_numLEDs {b,b,...,b}_numLEDs}_3]");

        addDescription(getServiceContract().STATUS_CONFIG, "chipType: [WS2801|WS2811|WS2812]\n frameDurationInMilliseconds: [0.." + Long.MAX_VALUE + "]\n clockFrequencyOfICsInHz: [10000..2000000]\n numberOfLEDs: [1..320]\n channelMapping: [rgb|rbg|grb|gbr|brg|bgr]");

    }

    @Override
    public void messageArrived(String string, MqttMessage mm) throws Exception {
        byte[] payload = mm.getPayload();
        if (payload == null) {
            return;
        }
        try {
            if (string.startsWith(getServiceContract().INTENT_CONFIG)) {
                LEDStripDeviceConfig config = getMapper().readValue(payload, LEDStripDeviceConfig.class
                );
                getDevice().setConfig(config);
            }
            if (string.startsWith(getServiceContract().INTENT_LEDs)) {
                leds = (getMapper().readValue(payload, short[][].class));
                getDevice().readyToPublish(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(LEDStripService.class
                    .getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    @Override
    public void configurationChanged(LEDStripDeviceConfig config) {
        addStatus(getServiceContract().STATUS_CONFIG, config);
    }

    @Override
    public short[][] getLEDsToPublish() {
        return leds;
    }
}
