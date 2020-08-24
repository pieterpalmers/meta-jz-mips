require recipes-bsp/u-boot/u-boot-x1000.inc

PROVIDES += "u-boot"
RPROVIDES_${PN} += "u-boot"

UBOOT_BINARY = "u-boot.bin"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.bin"
UBOOT_SYMLINK = "u-boot-${MACHINE}.bin"

UBOOT_SPL_BINARY = "u-boot-spl.bin"
UBOOT_SPL_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}-spl.bin"
UBOOT_SPL_SYMLINK = "u-boot-${MACHINE}-spl.bin"

UBOOT_MERGED_BINARY = "u-boot-with-spl.bin"
UBOOT_MERGED_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}-with-spl.bin"
UBOOT_MERGED_SYMLINK = "u-boot-${MACHINE}-with-spl.bin"
# 
# UBOOT_GPT_BINARY = "u-boot-with-spl-mbr-gpt.bin"
# UBOOT_GPT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}-with-spl-mbr-gpt.bin"
# UBOOT_GPT_SYMLINK = "u-boot-${MACHINE}-with-spl-mbr-gpt.bin"

UBOOT_MAKE_TARGET = "all"

do_configure () {
    oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
}

do_compile () {
    oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}
}

do_install () {
	install -d ${D}/boot
	install ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
	install ${B}/spl/${UBOOT_SPL_BINARY} ${D}/boot/${UBOOT_SPL_IMAGE}
	
	cd ${D}/boot
	ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
	ln -sf ${UBOOT_SPL_IMAGE} ${UBOOT_SPL_BINARY}

	if [ -e ${WORKDIR}/fw_env.config ] ; then
		install -d ${D}${sysconfdir}
		install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
	fi

}

FILES_${PN} = "/boot ${sysconfdir}"
# no gnu_hash in uboot.bin, by design, so skip QA
INSANE_SKIP_${PN} = "1"

inherit deploy

addtask deploy before do_package after do_compile

do_deploy () {
	install -d ${DEPLOYDIR}
	install ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
	install ${B}/spl/${UBOOT_SPL_BINARY} ${DEPLOYDIR}/${UBOOT_SPL_IMAGE}
	install ${B}/${UBOOT_MERGED_BINARY} ${DEPLOYDIR}/${UBOOT_MERGED_IMAGE}
# 	install ${B}/${UBOOT_GPT_BINARY} ${DEPLOYDIR}/${UBOOT_GPT_IMAGE}

	cd ${DEPLOYDIR}
	rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
	
	rm -f ${UBOOT_SPL_BINARY} ${UBOOT_SPL_SYMLINK}
	ln -sf ${UBOOT_SPL_IMAGE} ${UBOOT_SPL_SYMLINK}
	ln -sf ${UBOOT_SPL_IMAGE} ${UBOOT_SPL_BINARY}
	
	rm -f ${UBOOT_MERGED_BINARY} ${UBOOT_MERGED_SYMLINK}
	ln -sf ${UBOOT_MERGED_IMAGE} ${UBOOT_MERGED_SYMLINK}
	ln -sf ${UBOOT_MERGED_IMAGE} ${UBOOT_MERGED_BINARY}
# 
# 	rm -f ${UBOOT_GPT_BINARY} ${UBOOT_GPT_SYMLINK}
# 	ln -sf ${UBOOT_GPT_IMAGE} ${UBOOT_GPT_SYMLINK}
# 	ln -sf ${UBOOT_GPT_IMAGE} ${UBOOT_GPT_BINARY}
}
