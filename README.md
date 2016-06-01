# zg
On-line database for words whitelist and blacklist

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar zg-0.1.0-standalone.jar [args]

## Options

Current version of zg accepts only one command line option used to specify port
on which zg should accepts all HTTP request.

Usage:
-p     port number
--port port number

Other options are specified in the configuration file named zg.ini.

    [server]
    url-prefix=/

    [display]
    app-name=CCS Custom Dictionary
    emender-page=http://ccs-jenkins.gsslab.brq.redhat.com:6502/emender

This file contains two sections named `[server]` and `[display]`. In the
`[server]` section only one configuration parameter could be specified:
`url-prefix`. It could be changed in situations where zg is accessible via
httpd under different URL like `http://my.domain.org/zg/`. In this case the
`url-prefix` should be set to `zg/`.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2016 Pavel Tisnovsky, Red Hat

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

