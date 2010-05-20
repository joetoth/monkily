VERSION=1.0

xpi: copy monkily-$(VERSION).xpi

copy: clean
	mkdir -p build
	cp -r chrome chrome.manifest install.rdf skin ./build
	sed -i 's/__VERSION__/${VERSION}/' build/install.rdf
	find build -name '.svn' | xargs rm -rf

clean:
	$(RM) -rf build

monkily-${VERSION}.xpi: $(PRE_REQS)
	cd build; zip -r $@ chrome chrome.manifest install.rdf skin -x \*.svn\* -x \*.in -x \*.psd -x Makefile -x \*.in* -x license.txt
