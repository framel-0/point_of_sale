package net.sipconsult.pos.ui.products

import net.sipconsult.pos.data.models.LoggedInUser

class AuthResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)
