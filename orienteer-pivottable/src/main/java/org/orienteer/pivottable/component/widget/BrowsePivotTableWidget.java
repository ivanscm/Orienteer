package org.orienteer.pivottable.component.widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.pivottable.PivotTableModule;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Widget for PivotTable widget for browse page
 */
@Widget(id = "browse-pivot-table", domain = "browse", oClass = PivotTableModule.WIDGET_OCLASS_NAME, order = 10, autoEnable = false)
public class BrowsePivotTableWidget extends AbstractPivotTableWidget<OClass> {

    public BrowsePivotTableWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

}
