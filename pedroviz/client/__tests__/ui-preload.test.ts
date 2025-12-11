// This is not a test, but is the preload for the testing for UI world...
import { GlobalRegistrator } from '@happy-dom/global-registrator';
const oldConsole = console;
GlobalRegistrator.register();
window.console = oldConsole;
