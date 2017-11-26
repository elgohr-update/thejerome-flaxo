package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.model.Echo
import org.apache.http.client.fluent.Content
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import java.util.Random

@RestController
class FlaxoController {

    @Value("\${client.id}") lateinit var clientId: String
    @Value("\${client.secret}") lateinit var clientSecret: String
    @Value("\${redirect.uri}") lateinit var redirectUri: String
    @Value("\${home.page}") lateinit var homePage: String

    @GetMapping("/")
    fun index() = Echo("Hello world!")

    @GetMapping("/echo")
    fun echo(@RequestParam("message") message: String) = Echo(message)

    @GetMapping("/github/auth")
    fun githubAuth(model: ModelMap): ModelAndView {
        model.addAllAttributes(mapOf(
                "client_id" to clientId,
                "redirect_uri" to redirectUri,
                "state" to Random().nextInt().toString()
        ))
        return ModelAndView("redirect:/http://github.com/login/oauth/authorize", model)
    }

    @GetMapping("/github/auth/code")
    fun githubAuthToken(@RequestParam("code") code: String, @RequestParam("state") state: String) {
        val content: Content = Request.Post("https://github.com/login/oauth/access_token")
                .bodyForm(
                        Form.form().apply {
                            add("client_id", clientId)
                            add("client_secret", clientSecret)
                            add("code", code)
                            add("redirect_uri", homePage)
                            add("state", state)
                        }.build()
                )
                .execute()
                .returnContent()

        println(content)
    }
}