/*This interface is created to be used as a callback for the AsyncTask.
  The data is fetched in the vackground thread in doInBackground.
  If the main thread starts execution of the next statement before the data is completely loaded,
  the program may crash or display incomplete results.
  So this callback is used which is executed in postExecute which guarantees that
  the data is loaded before the execution of next statements.
 */

package com.capstone;

public interface SourceCallBack {

    void addData();
}
