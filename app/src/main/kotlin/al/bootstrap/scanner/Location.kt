package al.bootstrap.scanner

abstract class Location

data class PointLocation(val offset: Int, val line: Int, val column: Int) : Location()

data class RangeLocation(val start: PointLocation, val end: PointLocation) : Location()

fun locationFrom(startOffset: Int, startLine: Int, startColumn: Int, endOffset: Int, endLine: Int, endColumn: Int): Location =
    if (startOffset == endOffset)
        PointLocation(startOffset, startLine, startColumn)
    else
        RangeLocation(PointLocation(startOffset, startLine, startColumn), PointLocation(endOffset, endLine, endColumn))
