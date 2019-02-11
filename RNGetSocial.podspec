require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "RNGetSocial"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  RNGetSocial
                   DESC
  s.summary = 'Get social RN module for iOS' 
  s.homepage     = "https://github.com/nmchr7/react-native-get-social.git"
  s.license      = "MIT"
  s.author       = { "author" => "nmchr7@gmail.com" }
  s.platform     = :ios, "8.0"
  s.source       = { :git => "https://github.com/nmchr7/react-native-get-social.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "GetSocial/Core"
  s.dependency "GetSocial/UI"
end

