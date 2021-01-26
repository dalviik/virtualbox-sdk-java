package vbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.virtualbox_6_1.AccessMode;
import org.virtualbox_6_1.CleanupMode;
import org.virtualbox_6_1.DeviceType;
import org.virtualbox_6_1.IMachine;
import org.virtualbox_6_1.IMedium;
import org.virtualbox_6_1.IProgress;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.IVirtualBox;

import org.virtualbox_6_1.LockType;
import org.virtualbox_6_1.MediumVariant;
import org.virtualbox_6_1.StorageBus;

import org.virtualbox_6_1.VBoxException;
import org.virtualbox_6_1.VirtualBoxManager;

public class TestVBox {
	

	public static void main(String[] args) {

		Scanner lee = new Scanner(System.in);
		VirtualBoxManager virtualBoxManager = VirtualBoxManager.createInstance(null);
		
		String opc = "";

		do {
			System.out.println("\n\nIngrese que opcion desea ejecutar");

			System.out.println("1) Levantar maquina");
			System.out.println("2) Crear maquina");
			System.out.println("3) Eliminar Maquina");
			System.out.println("0) Salir");
			
			opc = lee.nextLine();
			switch (opc) {
			case "1":
				System.out.println("\nHas seleccionado levantar la maquina");
				levantarMaquina(virtualBoxManager,lee);

				break;

			case "2":
				System.out.println("Has seleccionado crear una nueva maquina");
				crearMaquina(virtualBoxManager,lee);
				break;

			case "3":
				System.out.println("Has seleccionado Eliminar una maquina");
				eliminarMaquina(virtualBoxManager,lee);
				break;

			case "0":
				
				virtualBoxManager.cleanup();
				System.exit(0);
				break;
			default:
				System.out.println("No se selecciono ninguna opcion valida");
				break;
			}
		} while (opc != "0");

	}

	public static void levantarMaquina(VirtualBoxManager virtualBoxManager, Scanner lee) {

		System.out.println("Levantando maquina por defecto....");

		String encontrarMaquina = "";

		System.out.println("Ingrese el nombre de la maquina que desea levantar");

		encontrarMaquina = lee.nextLine();
		
		try {
			
			IVirtualBox vbox = virtualBoxManager.getVBox();
			try {
				IMachine iMachine = vbox.findMachine(encontrarMaquina);

				ISession session = virtualBoxManager.getSessionObject();
				ArrayList<String> env = new ArrayList<String>();

				IProgress launchVMProcess = iMachine.launchVMProcess(session, "gui", env);


				System.out.println("\n**** INICIANDO MAQUINA VIRTUAL ****\n");
				launchVMProcess.waitForCompletion(-1);

			} catch (Exception e) {
				System.out.println("No se encontró la maquina");				
			}

		} catch (VBoxException e) {
			
			System.out.println("Java stack trace:");
			e.printStackTrace();
		} catch (RuntimeException e) {
			System.out.println("Runtime error: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void crearMaquina(VirtualBoxManager virtualBoxManager, Scanner lee) {

		
		String nameHDController = "Hard Disk Controller";
		String nameDVDController = "DVD Controller";

		System.out.println("Ingrese el nombre de la maquina virtual");	
		String nameMachine = lee.nextLine();
		
		

		try {
			IVirtualBox iVirtualBox = virtualBoxManager.getVBox();
			ISession session = virtualBoxManager.getSessionObject();
			IMachine crearMaquina = iVirtualBox.createMachine(null, nameMachine, null, null, null);

			crearMaquina.setMemorySize(1024L);
			crearMaquina.setOSTypeId("Ubuntu_64");

			iVirtualBox.registerMachine(crearMaquina);

			crearMaquina.lockMachine(session, LockType.Write);

			IMachine sessionMachine = session.getMachine();

			try {
				IMedium hardDisk = iVirtualBox.createMedium("vdi",
						"/home/dalviik/Workspaces/Proys/JALA-BOOTCAMP/CI/DISTROS/" + nameMachine
								+ "/HardDiskBetaMachine.vdi",
						AccessMode.ReadWrite, DeviceType.HardDisk);

				List<MediumVariant> mediumVariants = new ArrayList<MediumVariant>();

				mediumVariants.add(MediumVariant.Standard);

				
				IProgress iProgress = hardDisk.createBaseStorage(10L * 1024L * 1024L * 1024L, mediumVariants);
				iProgress.waitForCompletion(-1);
				Integer resultCode = iProgress.getResultCode();
				System.out.println("ResultCode: " + resultCode);
				System.out.println("Se ha creado la maquina");
				sessionMachine.addStorageController(nameHDController, StorageBus.SATA);

				sessionMachine.attachDevice(nameHDController, 0, 0, DeviceType.HardDisk, hardDisk);

				
				sessionMachine.addStorageController(nameDVDController, StorageBus.IDE);

				IMedium dvdImage = iVirtualBox.openMedium(
						"/home/dalviik/Downloads/Distros/ubuntu-20.10-live-server-amd64.iso", DeviceType.DVD,
						AccessMode.ReadOnly, Boolean.FALSE);

				sessionMachine.attachDevice(nameDVDController, 1, 0, DeviceType.DVD, dvdImage);

				sessionMachine.setBootOrder(1L, DeviceType.DVD);
				sessionMachine.saveSettings();

			} catch (Exception e) {
				e.printStackTrace();
			}
			session.unlockMachine();
		} catch (Exception e) {
			System.out.println("Error getVbox");
		}
	}
	public static void eliminarMaquina(VirtualBoxManager virtualBoxManager, Scanner lee) {
		try {
			System.out.println("Ingresa el nombre de la maquina que quieres eliminar");
			String nombreMaquina = lee.nextLine();
			
			IVirtualBox iVirtualBox = virtualBoxManager.getVBox();
			ISession session = virtualBoxManager.getSessionObject();
			IMachine machineToDelete = iVirtualBox.findMachine(nombreMaquina);
			machineToDelete.lockMachine(session, LockType.Write);
			session.unlockMachine();
			List<IMedium> media = machineToDelete.unregister(CleanupMode.DetachAllReturnHardDisksOnly);
			for (IMedium iMedium : media) {
				System.out.println(iMedium.getName() + " - " + iMedium.getDescription() + " - " + iMedium.getFormat());
			}
			machineToDelete.deleteConfig(media);
			System.out
					.println("\n **** La maquina "+ nombreMaquina + " ha sido eliminada.****\n");
		} catch (Exception e) {
			System.out.println("No se encontro maquina" + e.getMessage());
		}
	}
}
