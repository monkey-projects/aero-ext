# Aero Extensions

[![Clojars Project](https://img.shields.io/clojars/v/com.monkeyprojects/aero-ext.svg)](https://clojars.org/com.monkeyprojects/aero-ext)

This is a Clojure lib that adds some more tag readers to use with [Aero](https://github.com/juxt/aero?tab=readme-ov-file).  This is a configuration library that reads `.edn` files but adds
some additional functionality to it.  For use with our own projects, we have
created this library, that adds some more extensions.

## Usage

First include the library:
```clojure
;; deps.edn
{:deps {com.monkeyprojects/aero-ext {:mvn/version "0.3.0"}}}
```
Or with Leiningen:
```clojure
:dependencies [[com.monkeyprojects/aero-ext "0.3.0"]]
```

Then enable the readers just by requiring the ns:
```clojure
(require '[monkey.aero])

;; Load something with aero
(require '[aero.core :as ac])

;; This will automatically process any extension readers
(ac/read-config "path/to/edn/file")
```

## Available Readers

These are the readers that are provided by this library.

### to-edn

Serializes the argument to an `edn` string.
```clojure
{:config-str #to-edn {:key "value"}}
```

### from-edn

Opposite of `#to-edn`, parses an `edn` string:
```clojure
{:config-str #from-edn "{:key \"value\"}"}
```

This is useful when you want to process an edn file before including it in your config
(like decrypting it).

### meta-merge

Similar to Aero's `#merge`, this performs a deep merge using
[meta-merge](https://github.com/weavejester/meta-merge).  Existing maps don't overwrite
eachother, but are merged again:
```clojure
{:value #meta-merge [{:first {:second "value"}}
                     {:first {:third "other value"}}]}
```
This will result in a map like this:
```clojure
{:value
 {:first
  {:second "value"
   :third "other value"}}}
```

See the [meta-merge docs](https://github.com/weavejester/meta-merge) for more details.
`#deep-merge` is a synonym, for backwards compatibility.

### deep-merge

See `meta-merge`.

### file

To include any file as a string in your config, use `#file`:
```clojure
{:log-config #file "path/to/file"}
```
This is not the same as the Aero-provided `#include`, because `#include`
will parse the file as `edn`, whereas `#file` will just read it as raw text.
This will use the `:resolver` as configured when reading the config.

### to-b64

This will encode the argument to Base64.
```clojure
{:password #to-b64 #env PASSWORD}
```
Useful for including binary data.

### from-b64

Similar to `#to-b64`, this will decode from Base64
```clojure
{:password #from-b64 #env PASSWORD}
```

### privkey

Will parse the argument as a PEM-encoded private key.  The result is a `java.security.PrivateKey`.
```clojure
{:private-key #privkey #file "ssh/private-key.pem"}
```

If the key is encrypted, you can specify the password by using a vector:
```clojure
{:private-key #privkey [#file "ssh/private-key.pem" "verysecretpass"]}
```

Returns a `java.security.PrivateKey`.

### pubkey

The counterpart of `privkey` for public `PEM` keys:
```clojure
{:public-key #pubkey #file "ssh/public-key.pem"}
```

Returns a `java.security.PublicKey`.

### str

Converts the argument to a string.  Useful in combination with `#from-b64`, which returns
a byte array.
```clojure
{:password #str #from-b64 #env PASSWORD}
```

### random

Selects an item at random from the argument list.
```clojure
{:continent #random ["europe" "asia" "africa" "north-america" "south-america" "oceania"]}
```
It has its uses ;-).

## License

Copyright (c) 2024 by [Monkey Projects BV](https://www.monkey-projects.be)

[MIT License](LICENSE)