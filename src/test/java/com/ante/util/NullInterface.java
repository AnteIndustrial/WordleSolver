package com.ante.util;

import com.ante.ui.WordleInterface;

public class NullInterface implements WordleInterface {

    @Override
    public void print(Object toPrint) {

    }

    @Override
    public String readLine() {
        return null;
    }
}
