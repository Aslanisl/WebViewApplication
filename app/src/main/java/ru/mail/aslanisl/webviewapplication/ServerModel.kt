package ru.mail.aslanisl.webviewapplication

data class ServerModel(
    val elementId: String? = null,
    val elementClass: String? = null,
    val elementHref: Boolean? = null,
    val hrefNumber: Int? = null,
    val element: String? = null,
    val hrefs: Boolean? = null
) {
    fun isClassIdModel(): Boolean {
        return (elementId != null || elementClass != null) &&
            elementHref == null && hrefNumber == null &&
            element == null && hrefs == null
    }

    fun isClassIdHrefModel(): Boolean {
        return (elementId != null || elementClass != null) &&
            elementHref == true && hrefNumber == null &&
            element == null && hrefs == null
    }

    fun isHrefNumberModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && hrefNumber != null &&
            element == null && hrefs == null
    }

    fun isElementModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && hrefNumber == null &&
            element != null && hrefs == null
    }

    fun isHrefsModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && hrefNumber == null &&
            element == null && hrefs == true
    }
}