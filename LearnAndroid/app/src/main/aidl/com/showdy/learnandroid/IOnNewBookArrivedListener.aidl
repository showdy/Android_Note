// IOnNewBookArrivedListener.aidl
package com.showdy.learnandroid;

// Declare any non-default types here with import statements
import com.showdy.learnandroid.Book;

interface IOnNewBookArrivedListener {
   void onNewBookArrived(in Book newBook);
}
