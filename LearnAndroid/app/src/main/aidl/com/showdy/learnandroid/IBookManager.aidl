// IBookManager.aidl
package com.showdy.learnandroid;

// Declare any non-default types here with import statements

import com.showdy.learnandroid.Book;
import com.showdy.learnandroid.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
