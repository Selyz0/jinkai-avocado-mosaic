package com.example.jinkai.avocado.filters;

import javax.swing.ImageIcon;

public interface FilterSAM {
	public abstract ImageIcon filter(ImageIcon img, int x0, int y0, int x1, int y1);
}
