package com.example.pdfapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class MainActivity extends AppCompatActivity {

    EditText editTextName, getEditTextAge, editTextNumber, editTextLocation;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = findViewById(R.id.editTextName);
        getEditTextAge = findViewById(R.id.editTextAge);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextNumber = findViewById(R.id.editTextNumber);
        submitButton = findViewById(R.id.button);

        submitButton.setOnClickListener(v -> {
            try {
                createPdf(editTextName.getText().toString(), getEditTextAge.getText().toString(), editTextNumber.getText().toString(), editTextLocation.getText().toString());
            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createPdf(String name, String age, String number, String location) throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "myPDF.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0, 0, 0, 0);

        Drawable d = getDrawable(R.drawable.touristimg);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph visitorTicket = new Paragraph("Visitor Ticket").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);
        Paragraph goup = new Paragraph("Tourism Department\n" + "Govement of Utter Pradesh, India").setTextAlignment(TextAlignment.CENTER).setFontSize(12);
        Paragraph varansi = new Paragraph("Varanasi").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER);

        float[] width = {100f, 100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell().add(new Paragraph("Visitor Name")));
        table.addCell(new Cell().add(new Paragraph(name)));

        table.addCell(new Cell().add(new Paragraph("Age")));
        table.addCell(new Cell().add(new Paragraph(age)));

        table.addCell(new Cell().add(new Paragraph("Mobile No.")));
        table.addCell(new Cell().add(new Paragraph(number)));

        table.addCell(new Cell().add(new Paragraph("Location")));
        table.addCell(new Cell().add(new Paragraph(location)));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd//MM/yyyy");
        table.addCell(new Cell().add(new Paragraph("Date:")));
        table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateFormatter).toString())));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a");
        table.addCell(new Cell().add(new Paragraph("Time:")));
        table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter).toString())));


        BarcodeQRCode qrCode = new BarcodeQRCode(name + "\n" + age + "\n" + number + "\n" + location + "\n" + LocalDate.now().format(dateFormatter) + "\n" + LocalTime.now().format(timeFormatter));
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        Image qrCodeImage = new Image(qrCodeObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.add(visitorTicket);
        document.add(goup);
        document.add(table);
        document.add(qrCodeImage);
        document.close();
        Toast.makeText(this, "Pdf Created", Toast.LENGTH_LONG).show();
    }
}