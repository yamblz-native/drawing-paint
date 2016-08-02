package ru.yandex.yamblz.ui.drawing;

import android.graphics.Bitmap;

public interface Drawer {

    enum Tool {
        BRUSH("brush"),
        ERASER("eraser"),
        NO("no");

        String mName;

        Tool(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }

        public static Tool findByName(String name) {
            for(Tool tool : values()) {
                if(tool.getName().equals(name)) {
                    return tool;
                }
            }
            return null;
        }
    }

    enum Filter {
        GRAYSCALE("grayscale"),
        SEPIA("sepia"),
        BINARY("binary"),
        INVERT("invert");

        String mName;

        Filter(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }

        public static String[] getFilterNames() {
            String[] names = new String[values().length];
            int iter = 0;
            for(Filter filter : values()) {
                names[iter++] = filter.getName();
            }
            return names;
        }
    }

    /**
     * Sets stroke size
     * @param size size in px
     */
    void setSize(float size);

    /**
     * Returns current size
     * @return size in px
     */
    float getSize();

    void selectTool(Tool tool);

    Tool getTool();

    /**
     * Disable drawing
     */
    void disable();

    /**
     * Sets color
     * @param color the color
     */
    void setColor(int color);

    /**
     * Returns current color
     * @return current color, or 0 if no color set
     */
    int getColor();

    /**
     * Sets source bitmap
     * @param bitmap the bitmap
     */
    void setBitmap(Bitmap bitmap);

    Bitmap getBitmap();

    /**
     * Cleans canvas
     */
    void clean();

    void filter(Filter filter);

}
