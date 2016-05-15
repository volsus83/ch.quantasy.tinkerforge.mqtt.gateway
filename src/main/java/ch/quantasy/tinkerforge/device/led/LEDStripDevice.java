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
package ch.quantasy.tinkerforge.device.led;

import ch.quantasy.tinkerforge.device.generic.GenericDevice;
import ch.quantasy.tinkerforge.stack.TinkerforgeStackAddress;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reto E. Koenig <reto.koenig@bfh.ch>
 */
public class LEDStripDevice extends GenericDevice<BrickletLEDStrip, LEDStripDeviceCallback> implements BrickletLEDStrip.FrameRenderedListener {

    public static final int DEFAULT_NUMBER_OF_LEDS = 50;

    // Frame duration in Milliseconds 20ms (1000ms / 50frames = 20ms per frame)
    public static final int DEFAULT_FRAME_DURATION_IN_MILLISECONDS = 20;

    // IC-refresh in Hz
    public static final int DEFAULT_CLOCK_FREQUENCY_OF_ICS_IN_HZ = 2000000;

    // ChipType
    public static final LEDStripDeviceConfig.ChipType DEFAULT_CHIP_TYPE = LEDStripDeviceConfig.ChipType.WS2801;

    public static final LEDStripDeviceConfig.ChannelMapping DEFAULT_CHANNEL_MAPPING = LEDStripDeviceConfig.ChannelMapping.RGB;

    private final static int NUMBER_OF_LEDS_PER_WRITE = 16;
    public final static int NUMBER_OF_COLOR_CHANNELS = 3;

    private int numberOfWrites;

    private LEDStripDeviceConfig config;

    private short[][][] frame;
    private Thread publisherThread;
    private final Publisher publisher;
    private boolean readyToSend;

    public LEDStripDevice(TinkerforgeStackAddress address, BrickletLEDStrip device) throws NotConnectedException, TimeoutException {
        super(address, device);
        this.publisher = new Publisher();
        System.out.println("Concurrent");
        config = new LEDStripDeviceConfig(LEDStripDevice.DEFAULT_CHIP_TYPE, LEDStripDevice.DEFAULT_CLOCK_FREQUENCY_OF_ICS_IN_HZ, LEDStripDevice.DEFAULT_FRAME_DURATION_IN_MILLISECONDS, LEDStripDevice.DEFAULT_NUMBER_OF_LEDS, LEDStripDevice.DEFAULT_CHANNEL_MAPPING);

    }

    @Override
    protected void addDeviceListeners() {
        getDevice().addFrameRenderedListener(this);
        try {
            this.setup();
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LEDStripDevice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void removeDeviceListeners() {
        this.readyToSend=false;
        getDevice().removeFrameRenderedListener(this);
    }

    private void setup() throws TimeoutException, NotConnectedException {
        setConfig(config);
        this.readyToSend = true;
        if (publisherThread == null) {
            publisherThread = new Thread(publisher);
            //publisherThread.setDaemon(true);
            publisherThread.start();
        }
    }

    public synchronized void setConfig(LEDStripDeviceConfig config) {
        try {
            if (config == null) {
                return;
            }
            this.config=config;
            setNumberOfLEDs(config.getNumberOfLEDs());
            setChipType(config.getChipType().getType());
            setClockFrequencyOfICsInHz(config.getClockFrequencyOfICsInHz());
            setFrameDurationInMilliseconds(config.getFrameDurationInMilliseconds());
            super.getCallback().configurationChanged(config);
        } catch (TimeoutException | NotConnectedException ex) {
            Logger.getLogger(LEDStripDevice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private synchronized void setNumberOfLEDs(final int numberOfLEDs) {
        this.numberOfWrites = (int) Math.ceil((double) numberOfLEDs
                / LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE);
        this.frame = new short[LEDStripDevice.NUMBER_OF_COLOR_CHANNELS][this.numberOfWrites][LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE];
    }

    private void setFrameDurationInMilliseconds(
            final int frameDurationInMilliseconds) throws TimeoutException, NotConnectedException {
        if (getDevice() != null) {
            getDevice()
                    .setFrameDuration(frameDurationInMilliseconds);

        }
    }

    private void setChipType(int chipType) throws TimeoutException, NotConnectedException {
        if (getDevice() != null) {
            getDevice().setChipType(chipType);
        }
    }

    private void setClockFrequencyOfICsInHz(final long clockFrequencyOfICsInHz) throws TimeoutException, NotConnectedException {
        if (getDevice() != null) {

            getDevice().setClockFrequency(clockFrequencyOfICsInHz);
        }
    }

    /**
     * This will guarantee the display of the set LEDs! in the next rendering!
     *
     * @param rgbLEDs
     */
    public synchronized void setRGBLEDs(final short[][] rgbLEDs) {
        LEDStripDeviceConfig localConfig = this.config;
        if (rgbLEDs == null) {
            return;
        }
        if (rgbLEDs.length != LEDStripDevice.NUMBER_OF_COLOR_CHANNELS) {
            return;
        }

        for (int colorChannel = 0; colorChannel < LEDStripDevice.NUMBER_OF_COLOR_CHANNELS; colorChannel++) {
            if ((rgbLEDs[colorChannel] == null)
                    || (rgbLEDs[colorChannel].length != localConfig.getNumberOfLEDs())) {
                return;
            }
        }

        for (int colorChannel = 0; colorChannel < LEDStripDevice.NUMBER_OF_COLOR_CHANNELS; colorChannel++) {
            for (int write = 0, remaining = localConfig.getNumberOfLEDs(); write < this.numberOfWrites; write++, remaining -= LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE) {
                System.arraycopy(rgbLEDs[colorChannel],
                        write
                        * LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE,
                        this.frame[colorChannel][write],
                        0,
                        Math.min(remaining,
                                LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE));
            }
        }
        sendRGBLEDFrame();
    }

    /**
     * This method is called via setRGBLEDs.
     */
    private synchronized void sendRGBLEDFrame() {
        if (getDevice() == null) {
            return;
        }

        while (!readyToSend) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                //Fine, we just go on
            }

        }
        try {
            for (int write = 0; write < this.numberOfWrites; write++) {

                getDevice()
                        .setRGBValues(write
                                * LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE,
                                (short) LEDStripDevice.NUMBER_OF_LEDS_PER_WRITE,
                                this.frame[config.getChannelMapping().mapTo(0)][write], this.frame[config.getChannelMapping().mapTo(1)][write],
                                this.frame[config.getChannelMapping().mapTo(2)][write]);
            }

        } catch (final TimeoutException e) {
            // Eh... ok
        } catch (final NotConnectedException e) {
            // Well...
        } catch (final NullPointerException e) {
            // Uups, device vanished...
        }
    }

    @Override
    public void frameRendered(final int length) {
        synchronized (this) {
            readyToSend = true;
            this.notifyAll();
        }
    }

    public void readyToPublish(LEDStripDeviceCallback callback) {
        publisher.readyToPublish(callback);
    }

    class Publisher implements Runnable {

        private final BlockingDeque<LEDStripDeviceCallback> publishingQueue;

        public Publisher() {
            this.publishingQueue = new LinkedBlockingDeque<>();
        }

        public synchronized void readyToPublish(LEDStripDeviceCallback callback) {
            if (this.publishingQueue.contains(callback)) {
                return;
            }
            this.publishingQueue.add(callback);
            return;
        }

        @Override
        public void run() {
            while (LEDStripDevice.this.isConnected()) {
                try {
                    LEDStripDeviceCallback callback = publishingQueue.take();
                    synchronized (LEDStripDevice.this) {
                        setRGBLEDs(callback.getLEDsToPublish());
                        sendRGBLEDFrame();

                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(LEDStripDevice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            LEDStripDevice.this.publisherThread = null;
            System.out.println("We are out...");

        }
    }
}
