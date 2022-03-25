package io.riccardomerolla.system.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

final case class Configuration(
    httpServer: HttpServerConfig,
    dbConfig: PostgresConfig
) derives ConfigReader