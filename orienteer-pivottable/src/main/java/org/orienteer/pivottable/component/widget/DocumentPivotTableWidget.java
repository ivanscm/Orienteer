package org.orienteer.pivottable.component.widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.Widget;
import org.orienteer.pivottable.PivotTableModule;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Widget for PivotTable widget for document page
 */
@Widget(id = "pivot-table", domain = "document", oClass = PivotTableModule.WIDGET_OCLASS_NAME, order = 10, autoEnable = false)
public class DocumentPivotTableWidget extends AbstractPivotTableWidget<OClass> {

    public DocumentPivotTableWidget(String id, IModel<OClass> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

}
