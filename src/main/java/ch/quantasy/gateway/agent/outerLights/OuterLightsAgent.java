/*
 * /*
 *  *   "TiMqWay"
 *  *
 *  *    TiMqWay(tm): A gateway to provide an MQTT-View for the Tinkerforge(tm) world (Tinkerforge-MQTT-Gateway).
 *  *
 *  *    Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *  *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *    Quellgasse 21, CH-2501 Biel, Switzerland
 *  *
 *  *    Licensed under Dual License consisting of:
 *  *    1. GNU Affero General Public License (AGPL) v3
 *  *    and
 *  *    2. Commercial license
 *  *
 *  *
 *  *    1. This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU Affero General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU Affero General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Affero General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *  *     accordance with the commercial license agreement provided with the
 *  *     Software or, alternatively, in accordance with the terms contained in
 *  *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *  *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *  *
 *  *
 *  *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *  *
 *  *
 */
package ch.quantasy.gateway.agent.outerLights;

import ch.quantasy.gateway.agent.led.AmbientLEDLightAgent;
import ch.quantasy.gateway.service.device.dc.DCServiceContract;
import ch.quantasy.gateway.service.device.linearPoti.LinearPotiServiceContract;
import ch.quantasy.gateway.service.stackManager.ManagerServiceContract;
import ch.quantasy.mqtt.gateway.client.ClientContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.GatewayClientEvent;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceClass;
import ch.quantasy.tinkerforge.stack.TinkerforgeStackAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import ch.quantasy.mqtt.gateway.client.MessageReceiver;

/**
 *
 * @author reto
 */
public class OuterLightsAgent {

    private final ManagerServiceContract managerServiceContract;
    private final DCServiceContract dcServiceContract;
    private final LinearPotiServiceContract linearPotiServiceContract;

    private final GatewayClient<ClientContract> gatewayClient;

    public OuterLightsAgent(URI mqttURI) throws MqttException {
        managerServiceContract = new ManagerServiceContract("Manager");

        dcServiceContract = new DCServiceContract("6kP5Zh", TinkerforgeDeviceClass.DC.toString());
        linearPotiServiceContract = new LinearPotiServiceContract("bxJ", TinkerforgeDeviceClass.LinearPoti.toString());

        gatewayClient = new GatewayClient(mqttURI, "wrth563g", new ClientContract("Agent", "OuterLights", "01"));
        gatewayClient.connect();
        connectRemoteServices("controller01","localhost");

        gatewayClient.addIntent(linearPotiServiceContract.INTENT_POSITION_CALLBACK_PERIOD, 100);
        gatewayClient.addIntent(dcServiceContract.INTENT_ACCELERATION, 20000);
        gatewayClient.addIntent(dcServiceContract.INTENT_DRIVER_MODE, 1);
        gatewayClient.addIntent(dcServiceContract.INTENT_PWM_FREQUENCY, 20000);
        gatewayClient.addIntent(dcServiceContract.INTENT_ENABLED, true);

        
        gatewayClient.subscribe(linearPotiServiceContract.EVENT_POSITION, new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] mm) throws Exception {
                GatewayClientEvent<Integer>[] positionEvents=gatewayClient.toEventArray(mm, Integer.class);
                int position=positionEvents[0].getValue();
                gatewayClient.addIntent(dcServiceContract.INTENT_VELOCITY_VELOCITY,(32767/100)*position);
            }
        });

    }

    private void connectRemoteServices(String... addresses) {
        for (String address : addresses) {
            gatewayClient.addIntent(managerServiceContract.INTENT_STACK_ADDRESS_ADD, new TinkerforgeStackAddress(address));
            try {
                //Bad idea! Better wait for the event of the managerService... having accepted the stack(s).
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AmbientLEDLightAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        URI mqttURI = URI.create("tcp://localhost:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);
        OuterLightsAgent agent = new OuterLightsAgent(mqttURI);
        System.in.read();
    }

}
