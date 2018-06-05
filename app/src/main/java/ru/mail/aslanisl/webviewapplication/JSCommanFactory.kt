package ru.mail.aslanisl.webviewapplication

object JSCommanFactory {

    fun generateCommands(models: List<ServerModel>?): List<JSCommand> {
        val commands = mutableListOf<JSCommand>()
        models?.forEach { model ->
            val command = when {
                model.isClassIdModel() -> getClassIdCommand(model)
                model.isClassIdHrefModel() -> getClassIdHrefCommand(model)
                model.isHrefNumberModel() -> getHrefNumberCommand(model)
                model.isElementModel() -> getElementCommand(model)
                model.isHrefsModel() -> getHrefsCommand()
                else -> null
            }
            command?.let { commands.add(it) }
        }

        return commands
    }

    private fun getClassIdCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                "var elementsClass = document.getElementsByClassName('${model.elementClass}');" +
                "if (elementsClass){" +
                    "for (var i = 0; i < elementsClass.length; i++) {" +
                        "elementsClass[i].click();" +
                    "}" +
                "}" +
                "var elementId = document.getElementById('${model.elementId}');" +
                "if (elementId) elementId.click();" +
            "})();"
        return JSCommand(command)
    }

    private fun getClassIdHrefCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                    "var clazzs = document.getElementsByClassName('${model.elementClass}');" +
                    "var classHref = null;" +
                    "for(var i=0; i < clazzs.length; i++) {" +
                        "if (clazzs[i].href) {" +
                            "classHref = clazzs[i].href;" +
                            "i = clazzs.length;" +
                        "}" +
                    "}" +
                    "var id = document.getElementById('${model.elementId}');" +
                    "var idHref = null;" +
                    "if (id) idHref = id.href;" +
                    "if (classHref){" +
                        "window.location.href = classHref;" +
                    "} else if (idHref) {" +
                        "window.location.href = idHref;" +
                    "}" +
            "})();"
        return JSCommand(command)
    }

    private fun getHrefNumberCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                "var links = document.links;" +
                "var hrefs = [];" +
                "for(var i=0; i < links.length; i++) {" +
                    "hrefs.push(links[i].href);" +
                "}" +
                "window.location.href = hrefs[${model.hrefNumber}]" +
            "})();"
        return JSCommand(command)
    }

    private fun getElementCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                "document.getElementsByTagName('${model.element}').click();" +
            "})();"
        return JSCommand(command)
    }

    private fun getHrefsCommand(): JSCommand {
        val command = "(function(){" +
                "var links = document.links;" +
                "var hrefs = [];" +
                "for(var i=0; i < links.length; i++) {" +
                    "hrefs.push(links[i].href);" +
                "}" +
                "Android.hrefsResponse(hrefs.join());" +
            "})();"
        return JSCommand(command)
    }
}