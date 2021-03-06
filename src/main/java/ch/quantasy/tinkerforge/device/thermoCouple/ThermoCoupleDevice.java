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
package ch.quantasy.tinkerforge.device.thermoCouple;

import ch.quantasy.tinkerforge.device.generic.GenericDevice;
import ch.quantasy.tinkerforge.stack.TinkerforgeStack;
import com.tinkerforge.BrickletThermocouple;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reto E. Koenig <reto.koenig@bfh.ch>
 */
public class ThermoCoupleDevice extends GenericDevice<BrickletThermocouple, ThermoCoupleDeviceCallback> {

    private DeviceConfiguration configuration;
    private Long callbackPeriod;
    private Long debouncePeriod;
    private DeviceTemperatureCallbackThreshold temperatureThreshold;

    public ThermoCoupleDevice(TinkerforgeStack stack, BrickletThermocouple device) throws NotConnectedException, TimeoutException {
        super(stack, device);
    }

    @Override
    protected void addDeviceListeners(BrickletThermocouple device) {
        device.addTemperatureListener(super.getCallback());
        device.addTemperatureReachedListener(super.getCallback());
        device.addErrorStateListener(super.getCallback());

        if (configuration != null) {
            setConfiguration(configuration);
        }
        if (callbackPeriod != null) {
            setTemperatureCallbackPeriod(this.callbackPeriod);
        }
        if (debouncePeriod != null) {
            setDebouncePeriod(debouncePeriod);
        }
        if (temperatureThreshold != null) {
            setTemperatureCallbackThreshold(temperatureThreshold);
        }

    }

    @Override
    protected void removeDeviceListeners(BrickletThermocouple device) {
        device.removeTemperatureListener(super.getCallback());
        device.removeTemperatureReachedListener(super.getCallback());
        device.removeErrorStateListener(super.getCallback());
    }

    public void setDebouncePeriod(Long period) {
        try {
            getDevice().setDebouncePeriod(period);
            this.debouncePeriod = getDevice().getDebouncePeriod();
            super.getCallback().debouncePeriodChanged(this.debouncePeriod);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(ThermoCoupleDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setTemperatureCallbackPeriod(Long period) {
        try {
            getDevice().setTemperatureCallbackPeriod(period);
            this.callbackPeriod = getDevice().getTemperatureCallbackPeriod();
            super.getCallback().temperatureCallbackPeriodChanged(this.callbackPeriod);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(ThermoCoupleDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setTemperatureCallbackThreshold(DeviceTemperatureCallbackThreshold threshold) {
        try {
            getDevice().setTemperatureCallbackThreshold(threshold.getOption(), threshold.getMin(), threshold.getMax());
            this.temperatureThreshold = new DeviceTemperatureCallbackThreshold(getDevice().getTemperatureCallbackThreshold());
            super.getCallback().temperatureCallbackThresholdChanged(this.temperatureThreshold);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(ThermoCoupleDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setConfiguration(DeviceConfiguration configuration) {
        try {
            getDevice().setConfiguration(configuration.getAveraging().getValue(), configuration.getType().getValue(),configuration.getFilter().getValue());
            this.configuration = new DeviceConfiguration(getDevice().getConfiguration());
            super.getCallback().configurationChanged(this.configuration);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(ThermoCoupleDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
