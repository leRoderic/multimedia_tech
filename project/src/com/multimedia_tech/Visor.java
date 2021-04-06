package com.multimedia_tech;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

public class Visor extends JFrame {

	BufferedImage imagen;

	public Visor(BufferedImage imagen) {

		JPanel contentPane = new JPanel();
		JLabel lblImageHolder = new JLabel("Image Holder");
		contentPane.add(lblImageHolder, BorderLayout.CENTER);
		this.setContentPane(contentPane);

		if (imagen != null) {
			ImageIcon icono = new ImageIcon((Image) imagen);
			lblImageHolder.setIcon(icono);
			this.setSize(icono.getIconWidth(), icono.getIconHeight());
		}

	}

}
