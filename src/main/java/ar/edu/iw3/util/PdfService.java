package ar.edu.iw3.util;

import ar.edu.iw3.model.persistence.LoadDataRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Phaser;

import ar.edu.iw3.model.Order;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    @Autowired
    private LoadDataRepository loadDataDAO;

    public byte[] conciliationPDF(Order order) throws DocumentException {
        // Formato de fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String fechaEmision = dateFormat.format(now);

        // Crear documento en memoria
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // HEADER
        addHeader(document, order, fechaEmision);

        // Información básica de la orden
        addOrderInfo(document, order);

        // Fechas importantes
        addImportantDates(document, order, dateFormat);

        // Información de último registro
        addLastRecordInfo(document, order, dateFormat);

        // Información de relaciones
        addRelationshipInfo(document, order);

        // Tabla para datos de carga (LoadData)
        addLoadDataSection(document, order, dateFormat);

        // Cerrar documento
        document.close();
        return baos.toByteArray();
    }

    public void addHeader(Document document, Order order, String fechaEmision) throws DocumentException {

        // Fuentes para el título y los datos
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Crear una tabla con dos columnas
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});

        // Celda para el título "INVOICE" alineado a la izquierda
        PdfPCell titleCell = new PdfPCell(new Phrase("Conciliación", boldFont));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(titleCell);

        // Celda para los datos "Invoice No" y "Invoice Date" alineados a la derecha
        PdfPCell dataCell = new PdfPCell();
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setPaddingLeft(70f);
        dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        // Añadir los datos en dos líneas dentro de la celda
        Paragraph invoiceNo = new Paragraph("Orden N°: " + order.getId(), normalFont);
        Paragraph invoiceDate = new Paragraph("Fecha de Emisión: " + fechaEmision, normalFont);
        dataCell.addElement(invoiceNo);
        dataCell.addElement(invoiceDate);

        headerTable.addCell(dataCell);

        // Añadir la tabla al documento
        document.add(headerTable);

        document.add(new Paragraph(" "));

        // Línea divisoria
        LineSeparator line = new LineSeparator();
        line.setPercentage(100);
        document.add(new Chunk(line));

        document.add(new Paragraph(" "));
    }
    public void addOrderInfo(Document document, Order order) throws DocumentException {
        // Título de la sección
        document.add(new Paragraph(" ")); // Espacio antes del título
        Paragraph subtitle = new Paragraph("Información de la Orden:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC));
        subtitle.setAlignment(Element.ALIGN_LEFT);
        document.add(subtitle);
        document.add(new Paragraph(" ")); // Espacio después del título

        // Crear una tabla para los datos
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setSpacingAfter(10f);
        infoTable.setWidths(new float[]{1, 3});

        // Estilos para celdas
        Font keyFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Datos de la orden
        addCellToTable(infoTable, "ID de Orden:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, String.valueOf(order.getId()), valueFont, Element.ALIGN_LEFT);

        addCellToTable(infoTable, "Contraseña:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, String.valueOf(order.getPassword()), valueFont, Element.ALIGN_LEFT);

        addCellToTable(infoTable, "Preset:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, String.valueOf(order.getPreset()), valueFont, Element.ALIGN_LEFT);

        addCellToTable(infoTable, "Tara:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, String.valueOf(order.getTare()), valueFont, Element.ALIGN_LEFT);

        addCellToTable(infoTable, "Peso Final:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, String.valueOf(order.getFinalWeight()), valueFont, Element.ALIGN_LEFT);

        addCellToTable(infoTable, "Estado:", keyFont, Element.ALIGN_LEFT);
        addCellToTable(infoTable, order.getState().toString(), valueFont, Element.ALIGN_LEFT);

        document.add(infoTable);
    }

    private void addCellToTable(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    public void addImportantDates(Document document, Order order, DateFormat dateFormat) throws DocumentException {
        // Espacio antes del título
        document.add(new Paragraph(" "));

        // Título de la sección
        Paragraph subtitle = new Paragraph("Historial de Fechas de la Orden:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC));
        subtitle.setAlignment(Element.ALIGN_LEFT);
        document.add(subtitle);

        // Espacio después del título
        document.add(new Paragraph(" "));

        // Crear una tabla para las fechas
        PdfPTable datesTable = new PdfPTable(2);
        datesTable.setWidthPercentage(100);
        datesTable.setSpacingBefore(10f);
        datesTable.setSpacingAfter(10f);
        datesTable.setWidths(new float[]{1, 2});

        // Estilos para celdas
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Añadir las fechas a la tabla
        addDateRow(datesTable, "Fecha de Recepción:", order.getDateReceived(), dateFormat, labelFont, dateFont);
        addDateRow(datesTable, "Fecha Primer Pesaje:", order.getDateFirstWeighing(), dateFormat, labelFont, dateFont);
        addDateRow(datesTable, "Fecha Carga Inicial:", order.getDateInitialCharge(), dateFormat, labelFont, dateFont);
        addDateRow(datesTable, "Fecha Carga Final:", order.getDateFinalCharge(), dateFormat, labelFont, dateFont);
        addDateRow(datesTable, "Fecha de Pesaje Final:", order.getDateFinalWeighing(), dateFormat, labelFont, dateFont);
        addDateRow(datesTable, "Fecha Esperada de Carga:", order.getExpectedChargeDate(), dateFormat, labelFont, dateFont);

        document.add(datesTable);
    }

    private void addDateRow(PdfPTable table, String label, Date date, DateFormat dateFormat, Font labelFont, Font dateFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);
        table.addCell(labelCell);

        String dateText = date != null ? dateFormat.format(date) : "N/A";
        PdfPCell dateCell = new PdfPCell(new Phrase(dateText, dateFont));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setPadding(5f);
        table.addCell(dateCell);
    }

    public void addLastRecordInfo(Document document, Order order, DateFormat dateFormat) throws DocumentException {
        // Espacio antes del título
        document.add(new Paragraph(" "));

        // Título de la sección
        Paragraph subtitle = new Paragraph("Información de Último Registro:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC));
        subtitle.setAlignment(Element.ALIGN_LEFT);
        document.add(subtitle);

        // Espacio después del título
        document.add(new Paragraph(" "));

        // Crear una tabla para la información de último registro
        PdfPTable recordTable = new PdfPTable(2);
        recordTable.setWidthPercentage(100);
        recordTable.setSpacingBefore(10f);
        recordTable.setSpacingAfter(10f);
        recordTable.setWidths(new float[]{1, 2});

        // Estilos para celdas
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Añadir información de último registro a la tabla
        addRecordRow(recordTable, "Peso Final de Carga:", String.valueOf(order.getFinalChargeWeight()), labelFont, valueFont);
        addRecordRow(recordTable, "Última Masa Acumulada:", String.valueOf(order.getLastAccumulatedMass()), labelFont, valueFont);
        addRecordRow(recordTable, "Última Densidad:", String.valueOf(order.getLastDensity()), labelFont, valueFont);
        addRecordRow(recordTable, "Última Temperatura:", String.valueOf(order.getLastTemperature()), labelFont, valueFont);
        addRecordRow(recordTable, "Último Caudal:", String.valueOf(order.getLastCaudal()), labelFont, valueFont);
        addRecordRow(recordTable, "Última Fecha:", order.getLastTimestamp() != null ? dateFormat.format(order.getLastTimestamp()) : "N/A", labelFont, valueFont);

        // Añadir la tabla al documento
        document.add(recordTable);
    }

    private void addRecordRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        table.addCell(valueCell);
    }

    public void addRelationshipInfo(Document document, Order order) throws DocumentException {
        // Espacio antes de la sección
        document.add(new Paragraph(" "));

        // Título principal de la sección
        Paragraph subtitle = new Paragraph("Información de Relacionada:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC));
        subtitle.setAlignment(Element.ALIGN_LEFT);
        document.add(subtitle);

        // Espacio después del título principal
        document.add(new Paragraph(" "));

        // Crear tabla de dos columnas para alinear los datos
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Configurar anchos de las columnas
        float[] columnWidths = {50f, 50f};
        table.setWidths(columnWidths);

        // Datos del "Chofer" y "Camión" en la primera columna
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("Chofer:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        leftCell.addElement(new Paragraph("Nombre: " + (order.getDriver() != null ? order.getDriver().getName() : "N/A"), new Font(Font.FontFamily.HELVETICA, 10)));
        leftCell.addElement(new Paragraph("DNI: " + (order.getDriver() != null ? order.getDriver().getDocument() : "N/A"), new Font(Font.FontFamily.HELVETICA, 10)));
        leftCell.addElement(new Paragraph(" "));
        leftCell.addElement(new Paragraph("Camión:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        leftCell.addElement(new Paragraph("Matrícula: " + (order.getTruck() != null ? order.getTruck().getPlate() : "N/A"), new Font(Font.FontFamily.HELVETICA, 10)));

        // Datos del "Cliente" y "Producto" en la segunda columna
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.addElement(new Paragraph("Cliente:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        rightCell.addElement(new Paragraph("Compañía: " + (order.getClient() != null ? order.getClient().getCompanyName() : "N/A"), new Font(Font.FontFamily.HELVETICA, 10)));
        rightCell.addElement(new Paragraph(" "));
        rightCell.addElement(new Paragraph("Producto:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        rightCell.addElement(new Paragraph("Nombre: " + (order.getProduct() != null ? order.getProduct().getName() : "N/A"), new Font(Font.FontFamily.HELVETICA, 10)));

        // Añadir las celdas a la tabla
        table.addCell(leftCell);
        table.addCell(rightCell);

        // Añadir la tabla al documento
        document.add(table);
    }

    public void addLoadDataSection(Document document, Order order, DateFormat dateFormat) throws DocumentException {
        BaseColor blanco = new BaseColor(255, 255, 255);

        // Subtítulo de la sección
        Paragraph title = new Paragraph("Datos de carga", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        title.setAlignment(Element.ALIGN_LEFT);
        document.add(title);

        // Fecha inicial de carga
        document.add(new Paragraph("Fecha de Carga Inicial: "
                + (order.getDateInitialCharge() != null ? dateFormat.format(order.getDateInitialCharge()) : "N/A")));
        document.add(new Paragraph(" "));

        // Crear la tabla con dos columnas
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Establecer los anchos relativos de las columnas
        float[] columnWidths = {0.7f, 0.3f}; // 70% para la primera columna, 30% para la segunda
        table.setWidths(columnWidths);

        // Encabezado de la tabla
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, blanco);

        PdfPCell paramHeader = new PdfPCell(new Phrase("Parámetro de Carga", headerFont));
        paramHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
        paramHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        paramHeader.setBorder(PdfPCell.NO_BORDER);
        paramHeader.setPadding(8f);
        table.addCell(paramHeader);

        PdfPCell valueHeader = new PdfPCell(new Phrase("Valor", headerFont));
        valueHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        valueHeader.setBorder(PdfPCell.NO_BORDER);
        valueHeader.setPadding(8f);
        table.addCell(valueHeader);

        // Fuente para las celdas de datos
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Filas de la tabla con parámetros y sus valores
        PdfPCell densityCell = new PdfPCell(new Phrase("Densidad Promedio", dataFont));
        densityCell.setBorder(PdfPCell.NO_BORDER);
        densityCell.setPadding(8f);
        table.addCell(densityCell);

        PdfPCell densityValueCell = new PdfPCell(new Phrase(String.valueOf(loadDataDAO.avgDensity(order.getId())), dataFont));
        densityValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        densityValueCell.setBorder(PdfPCell.NO_BORDER);
        densityValueCell.setPadding(8f);
        table.addCell(densityValueCell);

        PdfPCell tempCell = new PdfPCell(new Phrase("Temperatura Promedio", dataFont));
        tempCell.setBorder(PdfPCell.NO_BORDER);
        tempCell.setPadding(8f);
        table.addCell(tempCell);

        PdfPCell tempValueCell = new PdfPCell(new Phrase(String.valueOf(loadDataDAO.avgTemperature(order.getId())), dataFont));
        tempValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tempValueCell.setBorder(PdfPCell.NO_BORDER);
        tempValueCell.setPadding(8f);
        table.addCell(tempValueCell);

        PdfPCell caudalCell = new PdfPCell(new Phrase("Caudal Promedio", dataFont));
        caudalCell.setBorder(PdfPCell.NO_BORDER);
        caudalCell.setPadding(8f);
        table.addCell(caudalCell);

        PdfPCell caudalValueCell = new PdfPCell(new Phrase(String.valueOf(loadDataDAO.avgCaudal(order.getId())), dataFont));
        caudalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        caudalValueCell.setBorder(PdfPCell.NO_BORDER);
        caudalValueCell.setPadding(8f);
        table.addCell(caudalValueCell);

        PdfPCell DateFinalCell = new PdfPCell(new Phrase("Fecha de Carga Final", new Font(dataFont.getFamily(), dataFont.getSize(), Font.BOLD)));
        DateFinalCell.setBorder(PdfPCell.NO_BORDER);
        DateFinalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        DateFinalCell.setPadding(8f);
        table.addCell(DateFinalCell);

        PdfPCell dateFinalValueCell = new PdfPCell(new Phrase(order.getDateFinalWeighing() != null ? dateFormat.format(order.getDateFinalWeighing()) : "N/A", dataFont));
        dateFinalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dateFinalValueCell.setBorder(PdfPCell.NO_BORDER);
        dateFinalValueCell.setPadding(8f);
        table.addCell(dateFinalValueCell);

        document.add(table);
    }
}
