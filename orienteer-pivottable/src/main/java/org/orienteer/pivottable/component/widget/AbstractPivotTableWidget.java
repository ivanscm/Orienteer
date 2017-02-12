package org.orienteer.pivottable.component.widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.behavior.UpdateOnDashboardDisplayModeChangeBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.pivottable.PivotTableModule;
import org.orienteer.pivottable.component.PivotPanel;

import java.util.Collection;
import java.util.Locale;

/**
 * Widget for PivotTable
 */
public class AbstractPivotTableWidget<T> extends AbstractWidget<T> {

    protected String config;
    protected String customSQL;
    private double noCacheRnd;

    @Inject
    protected IOClassIntrospector oClassIntrospector;

    public AbstractPivotTableWidget(String id, IModel<T> model,
                                    IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

        noCacheRnd = Math.random();

        add(new PivotPanel("pivot", new PropertyModel<String>(this, "url"),
                new PropertyModel<DisplayMode>(this, "displayMode"),
                new PropertyModel<String>(this, "config")));
        add(UpdateOnDashboardDisplayModeChangeBehavior.INSTANCE);
    }

    protected String getUrl() {
        String sql = getSql();
        return "/orientdb/query/db/sql/" +
                UrlEncoder.PATH_INSTANCE.encode(sql, "UTF-8") +
                "/99999?rnd=" +
                noCacheRnd;
    }

    public String getSql() {
        if (Strings.isEmpty(customSQL)) {
            String thisLang = getLocale().getLanguage();
            String systemLang = Locale.getDefault().getLanguage();
            OClass oClass = (OClass) getModelObject();
            StringBuilder sb = new StringBuilder();
            Collection<OProperty> properties = oClass.properties();
            for (OProperty property : properties) {
                OType type = property.getType();
                if (Comparable.class.isAssignableFrom(type.getDefaultJavaType())) {
                    sb.append(property.getName()).append(", ");
                } else if (OType.LINK.equals(type)) {
                    OClass linkedClass = property.getLinkedClass();
                    OProperty nameProperty = oClassIntrospector.getNameProperty(linkedClass);
                    if (nameProperty != null) {
                        OType linkedClassType = nameProperty.getType();
                        String map = property.getName() + '.' + nameProperty.getName();
                        if (Comparable.class.isAssignableFrom(linkedClassType.getDefaultJavaType())) {
                            sb.append(map).append(", ");
                        } else if (OType.EMBEDDEDMAP.equals(linkedClassType)) {
                            sb.append("coalesce(").append(map).append('[').append(thisLang).append("], ");
                            if (!thisLang.equals(systemLang)) {
                                sb.append(map).append('[').append(systemLang).append("], ");
                            }
                            sb.append("first(").append(map).append(")) as ").append(property.getName()).append(", ");
                        }
                    }
                }
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2);
            sb.insert(0, "SELECT ");
            sb.append(" FROM ").append(oClass.getName());
            return sb.toString();
        } else {
            return customSQL;
        }
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.table);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.pivottable");
    }

    @Override
    public void loadSettings() {
        super.loadSettings();
        ODocument doc = getWidgetDocument();
        if (doc == null) return;
        config = doc.field(PivotTableModule.OPROPERTY_PIVOT_TABLE_CONFIG);
        customSQL = doc.field(PivotTableModule.OPROPERTY_PIVOT_CUSTOM_SQL);
    }

    @Override
    public void saveSettings() {
        super.saveSettings();
        ODocument doc = getWidgetDocument();
        if (doc == null) return;
        doc.field(PivotTableModule.OPROPERTY_PIVOT_TABLE_CONFIG, config);
        doc.field(PivotTableModule.OPROPERTY_PIVOT_CUSTOM_SQL, customSQL);
    }

    public DisplayMode getDisplayMode() {
        return getDashboardPanel().getModeObject();
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
