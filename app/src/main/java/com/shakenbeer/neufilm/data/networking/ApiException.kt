package com.shakenbeer.neufilm.data.networking

import java.io.IOException

class ApiException(message: String = "Unknown API error.") : IOException(message)