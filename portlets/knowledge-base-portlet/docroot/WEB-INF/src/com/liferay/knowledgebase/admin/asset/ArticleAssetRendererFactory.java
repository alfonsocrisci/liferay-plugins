/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.knowledgebase.admin.asset;

import com.liferay.knowledgebase.model.Article;
import com.liferay.knowledgebase.service.ArticleLocalServiceUtil;
import com.liferay.knowledgebase.service.permission.AdminPermission;
import com.liferay.knowledgebase.service.permission.ArticlePermission;
import com.liferay.knowledgebase.util.ActionKeys;
import com.liferay.knowledgebase.util.PortletKeys;
import com.liferay.knowledgebase.util.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Shin
 */
public class ArticleAssetRendererFactory extends BaseAssetRendererFactory {

	public static final String CLASS_NAME = Article.class.getName();

	public static final String TYPE = "article";

	public AssetRenderer getAssetRenderer(long classPK, int type)
		throws PortalException, SystemException {

		int status = WorkflowConstants.STATUS_ANY;

		if (type == TYPE_LATEST_APPROVED) {
			status = WorkflowConstants.STATUS_APPROVED;
		}

		Article article = ArticleLocalServiceUtil.getLatestArticle(
			classPK, status);

		return new ArticleAssetRenderer(article);
	}

	public String getClassName() {
		return CLASS_NAME;
	}

	public String getType() {
		return TYPE;
	}

	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException, SystemException {

		HttpServletRequest request =
			liferayPortletRequest.getHttpServletRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!AdminPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), ActionKeys.ADD_ARTICLE)) {

			return null;
		}

		PortletURL portletURL = PortletURLFactoryUtil.create(
			request, PortletKeys.KNOWLEDGE_BASE_ADMIN,
			getControlPanelPlid(themeDisplay), PortletRequest.RENDER_PHASE);

		portletURL.setParameter("jspPage", "/admin/edit_article.jsp");

		return portletURL;
	}

	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return ArticlePermission.contains(permissionChecker, classPK, actionId);
	}

	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPathThemeImages() + "/trees/page.png";
	}

}