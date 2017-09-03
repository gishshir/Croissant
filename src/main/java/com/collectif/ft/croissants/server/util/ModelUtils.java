package com.collectif.ft.croissants.server.util;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.server.business.AbstractModel;

public class ModelUtils {

	
	public static final <T extends AbstractModel<?>> List<Integer> listIds (List<T> listModel) {
		if (listModel == null) {
			return null;
		}
		List<Integer> listIds = new ArrayList<Integer>(listModel.size());
		
		for (AbstractModel<?> model : listModel) {
			listIds.add(model.getId());
		}
		
		return listIds;
	}
	
	/**
	 * ne garde que les n premiers
	 * @param listModel
	 * @param count
	 * @return
	 */
	public static final <T extends AbstractModel<?>> List<Integer> listIds (List<T> listModel, int count) {
		
		if (listModel == null) {
			return null;
		}
		List<Integer> listIds = new ArrayList<Integer>(listModel.size());
		count = Math.min(count, listModel.size());
		
		for (int i = 0; i < count; i++) {

			listIds.add(listModel.get(i).getId());
		}
		
		return listIds;
	}
}
