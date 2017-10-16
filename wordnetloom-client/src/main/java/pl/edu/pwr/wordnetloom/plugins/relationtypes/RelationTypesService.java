/*
    Copyright (C) 2011 Łukasz Jastrzębski, Paweł Koczan, Michał Marcińczuk,
                       Bartosz Broda, Maciej Piasecki, Adam Musiał,
                       Radosław Ramocki, Michał Stanek
    Part of the WordnetLoom

    This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 3 of the License, or (at your option)
any later version.

    This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE. 

    See the LICENSE and COPYING files for more details.
*/

package pl.edu.pwr.wordnetloom.plugins.relationtypes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import pl.edu.pwr.wordnetloom.utils.Labels;
import pl.edu.pwr.wordnetloom.plugins.relationtypes.frames.RelationsEditorFrame;
import pl.edu.pwr.wordnetloom.systems.ui.MenuItemExt;
import pl.edu.pwr.wordnetloom.workbench.abstracts.AbstractService;
import pl.edu.pwr.wordnetloom.workbench.interfaces.Workbench;

/**
 * serwis zajmujący sie obsluga edycji typow relacji
 * @author Max
 */
public class RelationTypesService extends AbstractService implements ActionListener {

	/**
	 * konstruktor serwisu
	 * @param workbench - srodowisko uruchomieniowe
	 */
	public RelationTypesService(Workbench workbench) {
		super(workbench);
	}

	/**
	 * instalacja akcji
	 */
	public void installMenuItems() {
		JMenu other = workbench.getMenu(Labels.OTHER);
		if (other==null) return;
		other.addSeparator();
		other.add(new MenuItemExt(Labels.EDIT_RELATION_TYPES,KeyEvent.VK_Y,this));
	}

	public boolean onClose() {
		return true;
	}

	public void installViews() {/***/}
	public void onStart() {/***/ }


	/**
	 * wywolanie akcji z menu
	 */
	public void actionPerformed(ActionEvent arg0) {
		RelationsEditorFrame.showModal(workbench);
	}
}