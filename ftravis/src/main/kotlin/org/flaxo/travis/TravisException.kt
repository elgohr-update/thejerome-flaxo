package org.flaxo.travis

import org.flaxo.core.language.Language

/**
 * Base travis exception.
 */
open class TravisException(message: String) : Throwable(message)

/**
 * Travis environment supplier unsupported language exception.
 */
class UnsupportedLanguageException(language: Language)
    : TravisException("Unsupported language ${language.name} to use with travis")