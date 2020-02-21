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

import android.content.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CMS50FWBluetoothConnectionManager {

    private static final String TAG = CMS50FWBluetoothConnectionManager.class.getSimpleName();
    private static final int STAY_CONNECTED_PERIOD_SEC = 5;

    private AndroidBluetoothConnectionComponents androidBluetoothConnectionComponents = null;
    private CMS50FWConnectionListener cms50FWConnectionListener = null;
    private boolean keepAliveTaskRunning;

    // They don't all have to be scheduled ExecutorServices but I
    // made them all the same for simplicity and consistency
    private ScheduledExecutorService generalPurposeExecutor = null;     // runs ResetTask and StopDataTask
    private ScheduledExecutorService readDataExecutor = null;           // runs and re-runs StartDataTask in an indefinite loop
    private ScheduledExecutorService keepAliveExecutor = null;          // runs KeepAliveTask every 5 minutes

    /**
     * Main constructor. You need an instance of this object in order to use
     * this library.
     *
     * @param bluetoothName try using: SpO202
     */
    public CMS50FWBluetoothConnectionManager(String bluetoothName) {
        this.cms50FWConnectionListener = new CMS50FWConnectionLogger();
        this.androidBluetoothConnectionComponents = new AndroidBluetoothConnectionComponents(this,
                this.cms50FWConnectionListener, bluetoothName);
    }

    public void setCMS50FWConnectionListener(CMS50FWConnectionListener cms50FWConnectionListener) {
        this.cms50FWConnectionListener = new ConnectionListenerForwarder(cms50FWConnectionListener);
        this.androidBluetoothConnectionComponents.setCms50FWConnectionListener(this.cms50FWConnectionListener);
    }

    /**
     * Most methods create tasks which are run and executed on
     * various executors. These methods are typically invoked from
     * the UI thread. A general rule in these methods is to shutdown executors
     * in the UI thread, but then submit Bluetooth component altering tasks to a
     * worker thread.
     **/

    /**
     * Invoke Bluetooth discovery, wait for it to finish, and then obtain a
     * Bluetooth socket and connect to the main Bluetooth service on the CMS50FW bluetooth device. Also
     * obtains IO streams. (These Bluetooth plumbing details are handled internally
     * so that you do not have to be aware of them.) After a successful
     * connection, as indicated by the callback {@link CMS50FWConnectionListener#onConnectionEstablished()},
     * your app can call {@link #startData()}. This occurs on the UI thread.
     */
    public void connect(Context context) throws BluetoothNotAvailableException, BluetoothNotEnabledException{
        androidBluetoothConnectionComponents.findAndConnect(context);
    }

    /**
     * Request data from the CMS50FW by issuing a start command on the
     * input stream. Also start the keep-alive service which pings the
     * CMS50FW every 5 seconds to ensure that its Bluetooth connection
     * remains alive.
     */
    public void startData() {
        androidBluetoothConnectionComponents.okToReadData = true;
        if (keepAliveExecutor == null ||
                keepAliveExecutor.isShutdown() ||
                keepAliveExecutor.isTerminated()) {
            keepAliveExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (!keepAliveTaskRunning) {
            keepAliveExecutor.scheduleAtFixedRate(new KeepAliveTask(androidBluetoothConnectionComponents),
                    0, STAY_CONNECTED_PERIOD_SEC, TimeUnit.SECONDS);
            keepAliveTaskRunning = true;
        }
        if (readDataExecutor == null) {
            readDataExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        readDataExecutor.submit(new StartDataTask(androidBluetoothConnectionComponents));
    }

    /**
     * Ask the CMS50FW to stop sending data by issuing a stop command on the
     * input stream. Also shutdown the keep-alive service.
     */
    public void stopData() {
        Util.safeShutdown(keepAliveExecutor);
        keepAliveTaskRunning = false;
        submitToGeneralExecutor(new StopDataTask(androidBluetoothConnectionComponents));
    }

    /**
     * Stop the data. Cancel discovery if ongoing. Shutdown the keep-alive service. Close IO
     * streams, etc. Resets and/or nullifies the Bluetooth connection and other components related
     * to it.
     * <p/>
     * In order to read data again after this method has been called, {@link #connect(android.content.Context)}
     * must be called again.
     */
    public void reset() {
        stopData();
        submitToGeneralExecutor(new ResetTask(androidBluetoothConnectionComponents));
    }

    /**
     * Shutdown and dispose of the executors and the
     * bluetooth connection manager object.
     */
    public void dispose(Context context) {
        Util.safeShutdown(keepAliveExecutor);
        Util.safeShutdown(readDataExecutor);
        Util.safeShutdown(generalPurposeExecutor);

        // since all executors have been shut down, call dispose on UI thread
        androidBluetoothConnectionComponents.dispose(context);
    }

    private void submitToGeneralExecutor(Runnable task) {
        if (generalPurposeExecutor == null || generalPurposeExecutor.isShutdown() || generalPurposeExecutor.isTerminated()) {
            generalPurposeExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        generalPurposeExecutor.submit(task);
    }
}
