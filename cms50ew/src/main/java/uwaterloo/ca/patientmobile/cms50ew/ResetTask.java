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
 * Closes the Bluetooth connection to the CMS50FW.
 * Also nullifies the Bluetooth socket, and IO streams.
 * <p>
 * Created by albertb on 1/19/2015.
 */
class ResetTask implements Runnable {

    private AndroidBluetoothConnectionComponents androidBluetoothConnectionComponents = null;

    public ResetTask(AndroidBluetoothConnectionComponents androidBluetoothConnectionComponents) {
        this.androidBluetoothConnectionComponents = androidBluetoothConnectionComponents;
    }

    @Override
    public void run() {
        androidBluetoothConnectionComponents.reset();
    }

}
