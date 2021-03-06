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
package ch.quantasy.gateway.service.device.LCD20x4;

import ch.quantasy.gateway.service.device.DeviceServiceContract;
import ch.quantasy.tinkerforge.device.LCD20x4.LCD20x4Device;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceClass;
import java.util.Map;

/**
 *
 * @author reto
 */
public class LCD20x4ServiceContract extends DeviceServiceContract {

    public final String BACKLIGHT;
    public final String INTENT_BACKLIGHT;
    public final String STATUS_BACKLIGHT;
    public final String CLEAR_DISPLAY;
    public final String INTENT_CLEAR_DISPLAY;
    public final String INTENT_CONFIG_PARAMETERS;
    public final String CONFIG_PARAMETERS;
    public final String STATUS_CONFIG_PARAMETERS;
    public final String INTENT_CUSTOM_CHARACTERS;
    public final String STATUS_CUSTOM_CHARACTERS;
    public final String CUSTOM_CHARACTERS;
    public final String DEFAULT_TEXT;
    public final String INTENT_DEFAULT_TEXT_TEXTS;
    public final String STATUS_DEFAULT_TEXT_TEXTS;
    public final String COUNTER;
    public final String INTENT_DEFAULT_TEXT_COUNTER;
    public final String STATUS_DEFAULT_TEXT_COUNTER;
    public final String WRITE_LINES;
    public final String INTENT_WRITE_LINES;
    public final String BUTTON;
    public final String PRESSED;
    public final String RELEASED;
    public final String EVENT_BUTTON_RELEASED;
    public final String EVENT_BUTTON_PRESSED;
    public final String TEXT;

    public LCD20x4ServiceContract(LCD20x4Device device) {
        this(device.getUid(), TinkerforgeDeviceClass.getDevice(device.getDevice()).toString());
    }

    public LCD20x4ServiceContract(String id) {
        this(id, TinkerforgeDeviceClass.LCD20x4.toString());
    }

    public LCD20x4ServiceContract(String id, String device) {
        super(id, device);

        BACKLIGHT = "backlight";
        INTENT_BACKLIGHT = INTENT + "/" + BACKLIGHT;
        STATUS_BACKLIGHT = STATUS + "/" + BACKLIGHT;

        CLEAR_DISPLAY = "clearDisplay";
        INTENT_CLEAR_DISPLAY = INTENT + "/" + CLEAR_DISPLAY;

        CONFIG_PARAMETERS = "configParameters";
        INTENT_CONFIG_PARAMETERS = INTENT + "/" + CONFIG_PARAMETERS;
        STATUS_CONFIG_PARAMETERS = STATUS + "/" + CONFIG_PARAMETERS;

        CUSTOM_CHARACTERS = "customCharacters";
        INTENT_CUSTOM_CHARACTERS = INTENT + "/" + CUSTOM_CHARACTERS;
        STATUS_CUSTOM_CHARACTERS = STATUS + "/" + CUSTOM_CHARACTERS;

        DEFAULT_TEXT = "defaultText";
        TEXT = "texts";
        INTENT_DEFAULT_TEXT_TEXTS = INTENT + "/" + DEFAULT_TEXT + "/" + TEXT;
        STATUS_DEFAULT_TEXT_TEXTS = STATUS + "/" + DEFAULT_TEXT + "/" + TEXT;

        COUNTER = "counter";
        INTENT_DEFAULT_TEXT_COUNTER = INTENT + "/" + DEFAULT_TEXT + "/" + COUNTER;
        STATUS_DEFAULT_TEXT_COUNTER = STATUS + "/" + DEFAULT_TEXT + "/" + COUNTER;

        WRITE_LINES = "writeLines";
        INTENT_WRITE_LINES = INTENT + "/" + WRITE_LINES;

        BUTTON = "button";
        PRESSED = "pressed";
        RELEASED = "released";
        EVENT_BUTTON_RELEASED = EVENT + "/" + BUTTON + "/" + RELEASED;
        EVENT_BUTTON_PRESSED = EVENT + "/" + BUTTON + "/" + PRESSED;
    }

    @Override
    protected void descirbeMore(Map<String, String> descriptions) {
        descriptions.put(INTENT_BACKLIGHT, "[true|false]");
        descriptions.put(STATUS_BACKLIGHT, "[true|false]");
        descriptions.put(INTENT_CLEAR_DISPLAY, "[true|false]");
        descriptions.put(INTENT_CONFIG_PARAMETERS, "cursor: [true|false]\n blinking: [true|false]");
        descriptions.put(STATUS_CONFIG_PARAMETERS, "cursor: [true|false]\n blinking: [true|false]");
        descriptions.put(INTENT_CUSTOM_CHARACTERS, "[index: [0..15]\n pixels: [[" + Short.MIN_VALUE + ".." + Short.MAX_VALUE + "]]_[1..8]]");
        descriptions.put(STATUS_CUSTOM_CHARACTERS, "[index: [0..15]\n pixels: [[" + Short.MIN_VALUE + ".." + Short.MAX_VALUE + "]]_[1..8]]");
        descriptions.put(INTENT_DEFAULT_TEXT_TEXTS, "[line: [0..3]\n text: [String]_[1..20]]");
        descriptions.put(STATUS_DEFAULT_TEXT_TEXTS, "[line: [0..3]\n text: [String]_[1..20]]");
        descriptions.put(INTENT_DEFAULT_TEXT_COUNTER, "[-1.." + Integer.MAX_VALUE + "]");
        descriptions.put(STATUS_DEFAULT_TEXT_COUNTER, "[-1.." + Integer.MAX_VALUE + "]");
        descriptions.put(INTENT_WRITE_LINES, "[line: [0..3]\n position: [0..18]\n text: [String]_[1..20]]");
    }
}
