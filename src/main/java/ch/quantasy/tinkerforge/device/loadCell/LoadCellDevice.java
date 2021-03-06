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
package ch.quantasy.tinkerforge.device.loadCell;

import ch.quantasy.tinkerforge.device.generic.GenericDevice;
import ch.quantasy.tinkerforge.stack.TinkerforgeStack;

import com.tinkerforge.BrickletLoadCell;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reto E. Koenig <reto.koenig@bfh.ch>
 */
public class LoadCellDevice extends GenericDevice<BrickletLoadCell, LoadCellDeviceCallback> {

    private DeviceConfiguration configuration;
    private Long callbackPeriod;
    private Long debouncePeriod;
    private DeviceWeightCallbackThreshold weightThreshold;
    private Short average;
        private Boolean isStatusLEDEnabled;


    public LoadCellDevice(TinkerforgeStack stack, BrickletLoadCell device) throws NotConnectedException, TimeoutException {
        super(stack, device);
    }

    @Override
    protected void addDeviceListeners(BrickletLoadCell device) {
        device.addWeightListener(super.getCallback());
        device.addWeightReachedListener(super.getCallback());
        if (average != null) {
            setMovingAverage(average);
        }
        if (configuration != null) {
            setConfiguration(configuration);
        }
        if (callbackPeriod != null) {
            setWeightCallbackPeriod(this.callbackPeriod);
        }
        if (debouncePeriod != null) {
            setDebouncePeriod(debouncePeriod);
        }
        if (weightThreshold != null) {
            setWeightCallbackThreshold(weightThreshold);
        }
        if(isStatusLEDEnabled!=null){
            setLED(isStatusLEDEnabled);
        }

    }

    @Override
    protected void removeDeviceListeners(BrickletLoadCell device) {
        device.removeWeightListener(super.getCallback());
        device.removeWeightReachedListener(super.getCallback());
    }

    public void setDebouncePeriod(Long period) {
        try {
            getDevice().setDebouncePeriod(period);
            this.debouncePeriod = getDevice().getDebouncePeriod();
            super.getCallback().debouncePeriodChanged(this.debouncePeriod);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setWeightCallbackPeriod(Long period) {
        try {
            getDevice().setWeightCallbackPeriod(period);
            this.callbackPeriod = getDevice().getWeightCallbackPeriod();
            super.getCallback().weightCallbackPeriodChanged(this.callbackPeriod);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setWeightCallbackThreshold(DeviceWeightCallbackThreshold threshold) {
        try {
            getDevice().setWeightCallbackThreshold(threshold.getOption(), threshold.getMin(), threshold.getMax());
            this.weightThreshold = new DeviceWeightCallbackThreshold(getDevice().getWeightCallbackThreshold());
            super.getCallback().weightCallbackThresholdChanged(this.weightThreshold);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setConfiguration(DeviceConfiguration configuration) {
        try {
            getDevice().setConfiguration(configuration.getGain().getValue(), configuration.getRate().getValue());
            this.configuration = new DeviceConfiguration(getDevice().getConfiguration());
            super.getCallback().configurationChanged(this.configuration);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setMovingAverage(Short average) {
        try {
            getDevice().setMovingAverage(average);
            this.average = getDevice().getMovingAverage();
            super.getCallback().movingAverageChanged(this.average);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tare(Boolean tare) {
        try {
            if (!tare) {
            }
            getDevice().tare();
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setLED(Boolean isStatusLEDEnabled) {
        try {
            if (isStatusLEDEnabled) {
                getDevice().ledOn();
            } else {
                getDevice().ledOff();
            }
            this.isStatusLEDEnabled = getDevice().isLEDOn();
            super.getCallback().statusLEDChanged(this.isStatusLEDEnabled);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public void calibrate(long value) {
		try {
			getDevice().calibrate(value);
		}catch(TimeoutException | NotConnectedException ex){
            Logger.getLogger(LoadCellDevice.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

}
