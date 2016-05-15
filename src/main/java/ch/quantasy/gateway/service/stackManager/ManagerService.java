/*
 *   "TiMqWay"
 *
 *    TiMqWay(tm): A gateway to provide an MQTT-View for the Tinkerforge(tm) world (Tinkerforge-MQTT-Gateway).
 *
 *    Copyright (c) 2015 Bern University of Applied Sciences (BFH),
 *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *    Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *    Licensed under Dual License consisting of:
 *    1. GNU Affero General Public License (AGPL) v3
 *    and
 *    2. Commercial license
 *
 *
 *    1. This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *     accordance with the commercial license agreement provided with the
 *     Software or, alternatively, in accordance with the terms contained in
 *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *
 *
 */
package ch.quantasy.gateway.service.stackManager;

import ch.quantasy.gateway.service.AbstractService;
import ch.quantasy.gateway.tinkerforge.TinkerForgeManager;
import ch.quantasy.gateway.tinkerforge.TinkerforgeFactoryListener;
import ch.quantasy.tinkerforge.device.TinkerforgeDevice;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceClass;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceListener;
import ch.quantasy.tinkerforge.stack.TinkerforgeStack;
import ch.quantasy.tinkerforge.stack.TinkerforgeStackAddress;
import ch.quantasy.tinkerforge.stack.TinkerforgeStackListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author reto
 */
public class ManagerService extends AbstractService<ManagerServiceContract> implements TinkerforgeFactoryListener, TinkerforgeStackListener, TinkerforgeDeviceListener {

    public ManagerService() throws MqttException {
        super(new ManagerServiceContract("Manager"), "TinkerforgeStackManager");
                addDescription(getServiceContract().INTENT_STACK_ADDRESS_ADD, "<address");
                addDescription(getServiceContract().INTENT_STACK_ADDRESS_REMOVE, "<address>");  
                addDescription(getServiceContract().EVENT_ADDRESS_CONNECTED, "<address>");
                addDescription(getServiceContract().EVENT_ADDRESS_DISCONNECTED, "<address>");
                addDescription(getServiceContract().EVENT_STACK_ADDRESS_ADDED, "<address>");
                addDescription(getServiceContract().EVENT_STACK_ADDRESS_REMOVED, "<address>");
                addDescription(getServiceContract().STATUS_STACK_ADDRESS+"/<address>/connected", "[true|false]");
                

                

        TinkerForgeManager.getInstance().addListener(this);
        updateStatus();
    }

    
    @Override
    public void messageArrived(String string, MqttMessage mm) throws Exception {
        byte[] payload = mm.getPayload();
        if (payload == null) {
            return;
        }
        try {
            if (string.startsWith(getServiceContract().INTENT_STACK_ADDRESS_ADD)) {

                String payloadString = new String(payload);
                TinkerforgeStackAddress address = getMapper().readValue(payloadString, TinkerforgeStackAddress.class);
                if (TinkerForgeManager.getInstance().containsStack(address)) {
                    return;
                }
                TinkerForgeManager.getInstance().addStack(address);
                System.out.println(">>" + new String(mm.getPayload()));
            }
            if (string.startsWith(getServiceContract().INTENT_STACK_ADDRESS_REMOVE)) {

                String payloadString = new String(payload);
                TinkerforgeStackAddress address = getMapper().readValue(payloadString, TinkerforgeStackAddress.class);
                if (!TinkerForgeManager.getInstance().containsStack(address)) {
                    return;
                }
                TinkerForgeManager.getInstance().removeStack(address);
                System.out.println(">>" + new String(mm.getPayload()));
            }

        } catch (IOException ex) {
            Logger.getLogger(ManagerService.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

    }

    public void connected(TinkerforgeStack stack) {
        TinkerforgeStackAddress address = stack.getStackAddress();
        String topic = getServiceContract().STATUS_STACK_ADDRESS + "/" + address.hostName + ":" + address.port;
        addStatus(topic, stack.isConnected());

    }

    @Override
    public void disconnected(TinkerforgeStack stack) {
        TinkerforgeStackAddress address = stack.getStackAddress();
        String topic = getServiceContract().STATUS_STACK_ADDRESS + "/" + address.hostName + ":" + address.port;
        addStatus(topic, stack.isConnected());
    }

    public void updateStatus() {
        Set<TinkerforgeStackAddress> addresses = TinkerForgeManager.getInstance().getStackAddresses();
        for (TinkerforgeStackAddress address : addresses) {
            TinkerforgeStack stack = TinkerForgeManager.getInstance().getStackFactory().getStack(address);
            this.stackAdded(stack);
        }
    }

    @Override
    public void stackAdded(TinkerforgeStack stack) {
        stack.addListener((TinkerforgeStackListener) this);
        stack.addListener((TinkerforgeDeviceListener) this);

        TinkerforgeStackAddress address = stack.getStackAddress();
        addStatus(getServiceContract().STATUS_STACK_ADDRESS + "/" + address.hostName + ":" + address.port, stack.isConnected());
        addEvent(getServiceContract().EVENT_STACK_ADDRESS_ADDED, address);
    }

    @Override
    public void stackRemoved(TinkerforgeStack stack) {

        if (stack == null) {
            return;
        }
        {
            stack.removeListener((TinkerforgeStackListener) this);
            TinkerforgeStackAddress address = stack.getStackAddress();
            String topic = getServiceContract().STATUS_STACK_ADDRESS + "/" + address.hostName + ":" + address.port;
            addStatus(topic, null);
            addEvent(getServiceContract().EVENT_STACK_ADDRESS_REMOVED, address);
        }
        for (TinkerforgeDevice device : stack.getDevices()) {
            device.removeListener(this);
            String topic = getServiceContract().STATUS_DEVICE + "/" + device.getAddress().hostName + "/" + TinkerforgeDeviceClass.getDevice(device.getDevice()) + "/" + device.getUid();
            addStatus(topic, null);
        }
    }

    private void updateDevice(TinkerforgeDevice device) {
        String topic = getServiceContract().STATUS_DEVICE + "/" + device.getAddress().hostName + "/" + TinkerforgeDeviceClass.getDevice(device.getDevice()) + "/" + device.getUid();
        addStatus(topic, device.isConnected());
    }

    @Override
    public void connected(TinkerforgeDevice device) {
        String topic = getServiceContract().STATUS_DEVICE + "/" + device.getAddress().hostName + "/" + TinkerforgeDeviceClass.getDevice(device.getDevice()) + "/" + device.getUid();
        addStatus(topic, true);
    }

    @Override
    public void reConnected(TinkerforgeDevice device) {
        String topic = getServiceContract().STATUS_DEVICE + "/" + device.getAddress().hostName + "/" + TinkerforgeDeviceClass.getDevice(device.getDevice()) + "/" + device.getUid();
        addStatus(topic, true);
    }

    @Override
    public void disconnected(TinkerforgeDevice device) {
        String topic = getServiceContract().STATUS_DEVICE + "/" + device.getAddress().hostName + "/" + TinkerforgeDeviceClass.getDevice(device.getDevice()) + "/" + device.getUid();
        addStatus(topic, false);
    }
}
