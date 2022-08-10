package al.bootstrap.scanner

sealed class Location {
    abstract operator fun plus(location: Location): Location
}

data class PointLocation(val offset: Int, val line: Int, val column: Int) : Location() {
    override operator fun plus(location: Location): Location =
        when (location) {
            is PointLocation ->
                if (location == this)
                    this
                else
                    RangeLocation(
                        PointLocation(Integer.min(offset, location.offset), Integer.min(line, location.line), Integer.min(column, location.column)),
                        PointLocation(Integer.max(offset, location.offset), Integer.max(line, location.line), Integer.max(column, location.column))
                    )

            is RangeLocation ->
                RangeLocation(
                    PointLocation(
                        Integer.min(offset, location.start.offset),
                        Integer.min(line, location.start.line),
                        Integer.min(column, location.start.column)
                    ),
                    PointLocation(
                        Integer.max(offset, location.end.offset),
                        Integer.max(line, location.end.line),
                        Integer.max(column, location.end.column)
                    )
                )
        }
}

data class RangeLocation(val start: PointLocation, val end: PointLocation) : Location() {
    override operator fun plus(location: Location): Location =
        when (location) {
            is PointLocation ->
                location + this

            is RangeLocation -> {
                val startIndex =
                    Integer.min(start.offset, location.start.offset)

                val endIndex =
                    Integer.max(end.offset, location.end.offset)

                val startLocation =
                    PointLocation(startIndex, Integer.min(start.line, location.start.line), Integer.min(start.column, location.start.column))

                val endLocation =
                    PointLocation(endIndex, Integer.max(end.line, location.end.line), Integer.max(end.column, location.end.column))

                RangeLocation(startLocation, endLocation)
            }
        }
}

fun locationFrom(startOffset: Int, startLine: Int, startColumn: Int, endOffset: Int, endLine: Int, endColumn: Int): Location =
    if (startOffset == endOffset)
        PointLocation(startOffset, startLine, startColumn)
    else
        RangeLocation(PointLocation(startOffset, startLine, startColumn), PointLocation(endOffset, endLine, endColumn))
