/*
 * Copyright (c) 2015 Albert C. Braun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uwaterloo.ca.patientmobile.cms50ew;

/**
 * Holds a collection of distinct properties  measured and written out by the CMS50FW
 * for each cycle of the 60HZ data stream:
 * <p/>
 * <ul>
 * <li><b>Pulse Wave Form:</b> (0-127) y values on the line graph</li>
 * <li><b>Pulse Intensity:</b> (0-15) height of bar on the bar graph</li>
 * <li><b>Pulse Rate:</b> (0-127) number of heartbeats per minute. <b>Please note: this library does not currently accurately report pulse rates above 127 BPM.</b></li>
 * <li><b>SpO2:</b> (0-100%) blood oxygen saturation level</li>
 * <li><b>Finger Sleeve Status:</b> (boolean) whether the finger being measured is positioned properly</li>
 * </ul>
 * <p>
 * In addition, there is a <i>time</i> property which is <i>not</i> generated by the
 * CMS50FW.
 * <p>
 * Please note that this library generates the timestamp when the data is read out of
 * the CMS50FW's bluetooth input stream. So, this timestamp marks a time
 * <i>slightly later</i> than the time when the accompanying data was actually measured
 * by the CMS50FW.
 * <p>
 * Please note that these properties are only a subset of all the available
 * data written by the CMS50FW.
 * <p>
 * Created by albertb on 1/11/2015.
 */
public class DataFrame {
    public final long time;
    public int pulseWaveForm;
    public int pulseIntensity;
    public int pulseRate;
    public int spo2Percentage;
    public boolean isFingerOutOfSleeve;

    DataFrame() {
        time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return Util.formatString("time:%s, spo2Percentage:%d, pulseRate:%d, pulseWaveForm:%d, pulseIntensity:%d, isFingerOutOfSleeve:%b",
                time, spo2Percentage, pulseRate, pulseWaveForm, pulseIntensity, isFingerOutOfSleeve);
    }
}