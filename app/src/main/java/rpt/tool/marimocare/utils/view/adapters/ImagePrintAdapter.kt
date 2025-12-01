package rpt.tool.marimocare.utils.view.adapters

import android.content.Context
import android.graphics.Bitmap
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.os.CancellationSignal
import android.graphics.Canvas
import java.io.FileOutputStream

class ImagePrintAdapter(private val context: Context, private val bitmap: Bitmap) :
    PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        callback: LayoutResultCallback,
        extras: android.os.Bundle?
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("qr_code_print.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>,
        destination: android.os.ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        val page = pdfDocument?.startPage(0) ?: return

        val canvas: Canvas = page.canvas

        // Scala il bitmap per adattarlo alla pagina
        val scale =
            (canvas.width.toFloat() / bitmap.width.toFloat())
                .coerceAtMost(canvas.height.toFloat() / bitmap.height.toFloat())
        val left = (canvas.width - bitmap.width * scale) / 2
        val top = (canvas.height - bitmap.height * scale) / 2

        canvas.save()
        canvas.translate(left, top)
        canvas.scale(scale, scale)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.restore()

        pdfDocument?.finishPage(page)

        try {
            pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: Exception) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument?.close()
            pdfDocument = null
        }

        callback.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
    }
}
