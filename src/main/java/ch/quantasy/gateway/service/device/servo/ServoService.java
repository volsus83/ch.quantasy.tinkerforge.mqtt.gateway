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
package ch.quantasy.gateway.service.device.servo;

import ch.quantasy.gateway.service.device.AbstractDeviceService;
import ch.quantasy.tinkerforge.device.servo.Servo;
import ch.quantasy.tinkerforge.device.servo.ServoDevice;
import ch.quantasy.tinkerforge.device.servo.ServoDeviceCallback;
import java.net.URI;
import java.util.Collection;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class ServoService extends AbstractDeviceService<ServoDevice, ServoServiceContract> implements ServoDeviceCallback {

    public ServoService(ServoDevice device, URI mqttURI) throws MqttException {
        super(mqttURI, device, new ServoServiceContract(device));
    }

    @Override
    public void messageReceived(String string, byte[] payload) throws Exception {

        if (string.startsWith(getContract().INTENT_STATUS_LED)) {
            Boolean enabled = getMapper().readValue(payload, Boolean.class);
            getDevice().setStatusLED(enabled);
        }
        if (string.startsWith(getContract().INTENT_SERVOS)) {
            Servo[] servos = getMapper().readValue(payload, Servo[].class);
            getDevice().setServos(servos);
        }
        if (string.startsWith(getContract().INTENT_MINIMUM_VOLTAGE)) {
            Integer value = getMapper().readValue(payload, Integer.class);
            getDevice().setMinimumVoltage(value);
        }
        if (string.startsWith(getContract().INTENT_OUTPUT_VOLTAGE)) {
            Integer value = getMapper().readValue(payload, Integer.class);
            getDevice().setOutputVoltage(value);
        }

    }

    @Override
    public void minimumVoltageChanged(Integer minimumVoltage) {
        publishStatus(getContract().STATUS_MINIMUM_VOLTAGE, minimumVoltage);

    }

    @Override
    public void underVoltage(int i) {
        publishEvent(getContract().EVENT_UNDERVOLTAGE, i);
    }

    @Override
    public void statusLEDChanged(Boolean statusLED) {
        publishStatus(getContract().STATUS_STATUS_LED, statusLED);
    }

    @Override
    public void outputVoltageChanged(Integer outputVoltage) {
        publishStatus(getContract().STATUS_OUTPUT_VOLTAGE, outputVoltage);

    }

    @Override
    public void positionReached(short s, short s1) {
        publishEvent(getContract().EVENT_POSITION_REACHED, new Position(s, s1));
    }

    @Override
    public void velocityReached(short s, short s1) {
        publishEvent(getContract().EVENT_VELOCITY_REACHED, new Velocity(s, s1));

    }

    @Override
    public void servosChanged(Collection<Servo> values) {
        publishStatus(getContract().STATUS_SERVOS, values);
    }

    public static class Position {

        private short id;
        private short position;

        private Position() {
        }

        public Position(short id, short position) {
            this.id = id;
            this.position = position;
        }

        public short getId() {
            return id;
        }

        public short getPosition() {
            return position;
        }

    }

    public static class Velocity {

        private short id;
        private short velocity;

        private Velocity() {
        }

        public Velocity(short id, short velocity) {
            this.id = id;
            this.velocity = velocity;
        }

        public short getId() {
            return id;
        }

        public short getVelocity() {
            return velocity;
        }

    }

}
