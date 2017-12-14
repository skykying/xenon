/**
 * 
 */
package com.abubusoft.xenon.game.ui;

import java.util.Locale;

import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.MeshGrid;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.BufferAllocationOptions;

/**
 * <p>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class UITextView extends UIViewComponent {
	
	protected static BufferAllocationOptions bufferOptions=BufferAllocationOptions.build().indexAllocation(BufferAllocationType.STATIC).textureAllocation(BufferAllocationType.DYNAMIC).vertexAllocation(BufferAllocationType.DYNAMIC);

	/**
	 * 
	 */
	public UITextView() {
		this("");
	}

	/**
	 * @param val
	 */
	public UITextView(String val) {
		super();

		// prima di tutto impostiamo la font
		font = BitmapFontManager.instance().defaultFont();
		maxLenght = font.options.maxLenght;

		// per eventuale conversione in uppercase
		setText(val);

		type = UIViewComponentType.TEXT;
	}

	public int getMaxLenght() {
		return maxLenght;
	}

	public void setMaxLenght(int maxLenght) {
		this.maxLenght = maxLenght;
	}

	public ArgonBitmapFont font;

	/**
	 * <p>
	 * Shape per il disegno
	 * </p>
	 */
	public MeshGrid shape;

	String value;

	private int maxLenght;

	public void setText(String text) {
		if (font.options.onlyUppercase) {
			text = text.toUpperCase(Locale.ENGLISH);
		}

		if (value!=null && value.equals(text)) {
			// ASSERT: il vecchio testo e quello attuale sono uguali
			return;
		}

		// aggiustiamo la lunghezza massima nel caso in cui sia maggiore della
		// lunghezza del testo
		if (maxLenght < text.length()) {
			// minimo tra 256 e len * 1.75
			maxLenght = (int) Math.min(maxLenght * 1.75, 256);
			// max tra lenght e testo (così siamo sicuri che sia incluso)
			maxLenght = (int) Math.max(maxLenght, text.length());

			shape = MeshFactory.createTiledGrid(1, 1, 1, maxLenght, MeshOptions.build().bufferAllocationOptions(bufferOptions));
		}

		// allochiamo shape la prima volta
		if (shape == null) {
			shape = MeshFactory.createTiledGrid(1, 1, 1, maxLenght, MeshOptions.build().bufferAllocationOptions(bufferOptions));
		}

		// imposta le dimensioni
		witdh = calculateLength(text);
		height = (int) font.data.lineHeight;

		value = text;

		valid = false;
	}

	public String getText() {
		return value;
	}

	/**
	 * @param windowWitdh
	 * @param windowHeight
	 */
	protected void prepareToDraw(float windowWitdh, float windowHeight) {
		preparePosition(windowWitdh, windowHeight, font.data.lineHeight);
		prepareText(windowWitdh, windowHeight);
	}

	/**
	 * Prepara il testo ad essere preparato per il draw. Calcola inoltre witdh e height del bounding box.
	 * 
	 * @param windowWitdh
	 * @param windowHeight
	 */
	private void prepareText(float windowWitdh, float windowHeight) {
		if (valid)
			return;

		// impostiamo il boundingbox iniziale
		// boundingBox.left = position.x;
		// boundingBox.right = boundingBox.left + witdh;

		// invertiamo per far funzionare il controllo
		// boundingBox.top = position.y - height;
		// boundingBox.bottom = position.y;

		// calcoliamo width e height
		height = (int) font.data.lineHeight;
		witdh = calculateLength(value);

		vertexCount = value.length() * MeshFactory.VERTEX_PER_QUAD;

		// i vertici hanno l'origine nel loro centro, i bounding box
		// la correzione del position e del bounding deve partire da questa considerazione

		// definiamo l'ancora del testo
		float ix = 0, iy = 0;

		// definisce l'ancora del testo
		switch (anchor.dockX) {
		case LEFT:
			ix = 0 + padding;

			break;
		case RIGHT:
			ix = -witdh - padding;

			break;
		case CENTER:

			ix = -witdh * 0.5f;

			break;
		case ABSOLUTE:
			ix = padding;

			break;
		}

		switch (anchor.dockY) {
		case BOTTOM:
			iy = height + padding;

			break;
		case CENTER:
			iy = height*0.5f;

			break;
		case TOP:
			iy = 0f - padding;

			break;
		case ABSOLUTE:
			iy = -padding;

			break;
		}

		//position.x += ix;
		//position.y += iy;

		boundingBox.left = ix;
		boundingBox.right = ix + witdh;

		// il bounding box ha le Y invertite. Dobbiamo quindi invertire la loro definizione
		// boundingBox.top = height*0.5f;
		// boundingBox.bottom = -height*0.5f;
		boundingBox.top = iy - height;
		boundingBox.bottom = iy;

		ArgonGlyph glyph;

		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) != ' ') {
				glyph = font.data.getGlyph(value.charAt(i));

				shape.setVertexCoords(0, i, ix, iy, glyph.width, glyph.height);
				shape.setTextureCoords(0, i, glyph.u, glyph.u2, glyph.v2, glyph.v);

				ix += glyph.width + glyph.xoffset;
			} else {
				shape.setVertexCoords(0, i, 0, 0, 0, 0);
				shape.setTextureCoords(0, i, 0, 0, 0, 0);
				
				ix += font.data.spaceWidth;
			}
		}

		// aggiorniamo shape
		shape.updateBuffers();
		
		// ora tutto è segnato
		valid=true;
	}

	/**
	 * <p>
	 * Calcola la lunghezza in pixel della stringa in base alla fonte attualmente utilizzata.
	 * </p>
	 * 
	 * @param introText
	 * @return
	 */
	private int calculateLength(String text) {
		int ix = 0;
		ArgonGlyph glyph;

		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) != ' ') {
				glyph = font.data.getGlyph(text.charAt(i));

				ix += glyph.width + glyph.xoffset;
			} else {
				ix += font.data.spaceWidth;
			}
		}

		return ix;
	}

	/**
	 * <p>
	 * numero di vertici usati
	 * </p>
	 */
	public int vertexCount;

	@Override
	public void update(long enlapsedTime, float speedAdapter) {
		// non devo fare niente
	}

	@Override
	public void resetUdate() {
		// non devo fare niente
	}

}
