(require '[monkey.ci.plugin.clj :as p])
(require '[monkey.ci.plugin.github :as gh])

[(p/deps-library)
 (gh/release-job {:dependencies ["publish"]})]
