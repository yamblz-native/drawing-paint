package ru.yandex.yamblz.ui.drawing;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * Interface for something that can draw
 */
public interface Drawer {

    enum Tool {
        BRUSH("brush"),
        ERASER("eraser"),
        STAMP("stamp"),
        NO("no");

        String mName;

        Tool(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }

        /**
         * Finds tool by the given name
         * @param name the name of a tool
         * @return the tool with the give name
         */
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

        /**
         * Returns the names of the filters
         * @return the names
         */
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

    /**
     * Selects the given tool
     * @param tool the tool
     */
    void selectTool(Tool tool);

    /**
     * Returns the selected tool
     * @return the tool
     */
    Tool getTool();

    /**
     * Select stamp tool with the given bitmap
     * @param stamp the bitmap
     */
    void selectStamp(Bitmap stamp);

    /**
     * Returns the stamp bitmap if {@link Tool#STAMP} selected
     * @return the stamp bitmap, or {@code null} if {@link Tool#STAMP} not selected
     */
    @Nullable Bitmap getStamp();

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

    /**
     * Returns the drawn bitmap
     * @return the drawn bitmap
     */
    Bitmap getBitmap();

    /**
     * Cleans canvas
     */
    void clean();

    /**
     * Applies the given filter to the image
     * @param filter the filter to apply
     */
    void filter(Filter filter);

}
