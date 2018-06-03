package ru.mail.aslanisl.webviewapplication

data class ServerModel(
    val elementId: String? = null,
    val elementClass: String? = null,
    val elementHref: String? = null,
    val HrefNumber: Int? = null,
    val element: String? = null,
    val hrefs: List<String>? = null
) {
    fun isClassIdModel(): Boolean {
        return (elementId != null || elementClass != null) &&
            elementHref == null && HrefNumber == null &&
            element == null && hrefs == null
    }

    fun isClassIdHrefModel(): Boolean {
        return (elementId != null || elementClass != null) &&
            elementHref != null && HrefNumber == null &&
            element == null && hrefs == null
    }

    fun isHrefNumberModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && HrefNumber != null &&
            element == null && hrefs == null
    }

    fun isElementModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && HrefNumber == null &&
            element != null && hrefs == null
    }

    fun isHrefsModel(): Boolean {
        return elementId == null && elementClass == null &&
            elementHref == null && HrefNumber == null &&
            element == null && hrefs != null
    }
}