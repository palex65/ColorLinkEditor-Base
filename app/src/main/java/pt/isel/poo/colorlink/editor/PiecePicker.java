package pt.isel.poo.colorlink.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import pt.isel.poo.colorlink.Img;
import pt.isel.poo.colorlink.R;
import pt.isel.poo.tile.Tile;
import pt.isel.poo.tile.TilePanel;

/**
 * A widget to select one of the piece types.
 */
public class PiecePicker extends TilePanel {
    public PiecePicker(Context ctx) {super(ctx); }
    public PiecePicker(Context ctx, AttributeSet attrs) { super(ctx, attrs); }
    public PiecePicker(Context ctx, AttributeSet attrs, int style) { super(ctx, attrs, style); }

    /**
     * Images of each type of piece
     */
    private final static int[] imgIds = {
            R.drawable.block, R.drawable.corner, R.drawable.empty,
            R.drawable.invert, R.drawable.link, R.drawable.side
    };

    private final int cols;               // Number of columns = "widthTiles" in layout
    private final SelectPiece[] tiles;    // One tile for each piece type
    private int selectedIdx = -1;   // Index of selected tile (-1 if none)

    { // Constructors code block
        cols = getWidthInTiles();
        tiles = new SelectPiece[imgIds.length];
        for(int i=0 ; i<tiles.length ; ++i)
            setTile(i%cols,i/cols, new SelectPiece(i));
    }

    /**
     * Changes the selected piece type
     * @param idx Index of slected piece (0..getMaxSelected())
     */
    public void setSelected(int idx) {
        if (selectedIdx==idx) return;
        if (idx<0 || idx>=tiles.length)
            throw new IllegalArgumentException("idx="+idx+" inválid");
        invalidateSelected();
        selectedIdx=idx;
    }

    /**
     * Returns the selected piece index
     * @return Index of elected piece
     */
    public int getSelected() { return selectedIdx; }

    /**
     * Returns the maximum value for index of selected piece.
     * @return maximum index
     */
    public int getMaxSelected() { return tiles.length; };

    /**
     * Changes the color of all pieces
     * @param color The new color
     */
    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    /**
     * Return the color of all pieces.
     * @return The current color
     */
    public int getColor() {
        return paint.getColor();
    }

    /**
     * Force repaint of selected piece
     */
    private void invalidateSelected() {
        if (selectedIdx!=-1)
            invalidate(selectedIdx%cols,selectedIdx/cols);
    }

    private Paint paint = new Paint(); // Used to draw pieces picture
    private Paint pSel = new Paint();  // Used to paint the border of selected piece
    { // Constructors code block
        paint.setColor(Color.YELLOW);
        pSel.setColor(Color.BLACK);
        pSel.setStyle(Paint.Style.STROKE);
    }

    /**
     * Implementation of Tile to represent each piece type.
     * Also implements Img.Updater to repaint the tile after asynchronously loading the image.
     */
    private class SelectPiece implements Tile, Img.Updater {
        private final Img img;          // The image of piece type
        private final int ordinal;      // Index of tile in parent array.

        public SelectPiece(int ord) {
            ordinal = ord;
            img = new Img(getContext(),imgIds[ord],this);
        }

        @Override   // interface Tile
        public void draw(Canvas canvas, int dim) {
            canvas.drawRect(0,0,dim,dim,paint); // Fills the area with the color of the piece.
            // The color appears in the transparent area of the image
            img.draw(canvas,dim,dim,paint);     // Draws image of piece over the color.
            if (ordinal==selectedIdx) {         // If seleceted draws the border
                pSel.setStrokeWidth(dim/10);    // Border thickness x2
                canvas.drawRect(0, 0, dim, dim, pSel);
            }
        }

        @Override   // iterface Tile
        public boolean setSelect(boolean selected) {
            if (selected)               // If clicked
                setSelected(ordinal);   // Indicates that this has been selected
            return selected;            // true --> Informs the panel to update tile
        }

        /**
         * Called by Img after loading image.
         * @param img
         */
        @Override   // interface Img.Updater
        public void updateImage(Img img) {
            invalidate(this);       // Request to repaint the tile
        }
    }
}
