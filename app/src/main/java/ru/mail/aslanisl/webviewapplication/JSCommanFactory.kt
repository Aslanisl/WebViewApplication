package ru.mail.aslanisl.webviewapplication

object JSCommanFactory {

    fun generateCommands(models: List<ServerModel>): List<JSCommand> {
        val commands = mutableListOf<JSCommand>()
        models.forEach { model ->
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
                "console.log(elementsClass);" +
                "if (elementsClass){" +
                    "for (var i = 0; i < elementsClass.length; i++) {" +
                        "elementsClass[i].click();" +
                    "}" +
                "}" +
                "var elementId = document.getElementById('${model.elementId}');" +
                "console.log(elementId);" +
            "if (elementId) elementId.click();" +
            "})();"
        return JSCommand(command)
    }

    private fun getClassIdHrefCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                "var classHref = document.querySelector('${model.elementClass}').href" +
                "var idHref = document.getElementById('${model.elementId}').href" +
                "if (classHref){" +
                    "window.location.href = classHref" +
                "} else {" +
                    "window.location.href = idHref" +
                "}" +
            "})();"
        return JSCommand(command)
    }

    private fun getHrefNumberCommand(model: ServerModel): JSCommand {
        val command = "(function(){" +
                "var links = document.links;" +
                "var hrefs = [];" +
                "for(var i=0; links<l.length; i++) {" +
                    "hrefs.push(links[i].href);" +
                "}" +
                "window.location.href = hrefs[${model.HrefNumber}]" +
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
                "for(var i=0; links<l.length; i++) {" +
                    "window.location.href = links[i].hre);" +
                "}" +
            "})();"
        return JSCommand(command)
    }
}