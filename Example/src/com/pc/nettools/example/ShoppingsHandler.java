package com.pc.nettools.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class ShoppingsHandler extends DefaultHandler {
    private static final String SHOPPINGS = "shoppings";
    private static final String SHOPPING = "shopping";
    private static final String ID = "id";
    private static final String NAME = "name";

    private ShoppingHandlerListener mListener;
    private ArrayList<Shopping> mShoppings;
    private Shopping mShopping;
    private String mCurrentElement;

    public ShoppingsHandler(ShoppingHandlerListener listener) {
        if (listener == null)
            throw new RuntimeException("listener can't be null");
        mListener = listener;
    }

    @Override
    public void startDocument() throws SAXException {
        mShoppings = new ArrayList<Shopping>();
    }

    @Override
    public void endDocument() throws SAXException {
        mListener.onParseShoppings(mShoppings);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        mCurrentElement = localName;

        if (localName.equals(SHOPPING))
            mShopping = new Shopping();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(SHOPPING))
            mShoppings.add(mShopping);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String string = new String(ch, start, length);

        if (mCurrentElement.equals(ID) && mShopping.getId() <= 0)
            mShopping.setId(Integer.parseInt(string));
        else if (mCurrentElement.equals(NAME) && mShopping.getName() == null)
            mShopping.setName(string);
    }

    public interface ShoppingHandlerListener {
        public void onParseShoppings(ArrayList<Shopping> shoppings);
    }
}