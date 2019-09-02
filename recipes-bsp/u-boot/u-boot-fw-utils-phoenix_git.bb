require recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

# No patches for other machines yet
COMPATIBLE_MACHINE = "phoenix"

SECURITY_LDFLAGS = "-z,relro -z,now"

PROVIDES += "u-boot-fw-utils"
RPROVIDES_${PN} += "u-boot-fw-utils"

BRANCH = "v8.2-20181116+ml"
SCMVERSION ?= "y"
SRCREV = "${AUTOREV}"

SRC_URI = "git:///${TOPDIR}/../ml-git/u-boot-ingenic;protocol=file;branch=${BRANCH}"

#SRC_URI += "file://0001-mmc-up-spt-text.patch \
#            file://mips_fixup.patch \
#"

PV = "${BRANCH}"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://README;beginline=2;endline=5;md5=a274fb029fabf05ebeb2bb92905ebe26"

S = "${WORKDIR}/git"
# NOTE: build in source dir to accomodate Ingenic changes
B = "${WORKDIR}/git"

TARGET_LDFLAGS=""
UBOOT_EXT = "bin"

PARALLEL_MAKE=""

UBOOT_MACHINE ?= "${MACHINE}_config"
UBOOT_BINARY = "u-boot.${UBOOT_EXT}"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.${UBOOT_EXT}"
UBOOT_SYMLINK = "u-boot-${MACHINE}.${UBOOT_EXT}"
UBOOT_MAKE_TARGET = "all"
UBOOT_SPL = "u-boot-spl.bin"
UBOOT_SPL_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}-spl.bin"

do_compile () {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake envtools
}

do_install () {
	install -d ${D}${base_sbindir}
	install -d ${D}${sysconfdir}
	install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
	install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
	install -m 0644 ${S}/tools/env/fw_env.config ${D}${sysconfdir}/fw_env.config
}

do_install_class-cross () {
	install -d ${D}${bindir_cross}
	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_DIRS_append_class-cross = " ${bindir_cross}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"
