import CustomContextPad from './CustomContextPad';
import CustomPalette from "./CustomPalette";
import CustomRenderer from "./CustomRenderer";

export default {
  __init__: [ 'paletteProvider','contextPadProvider','customRenderer'],
  paletteProvider: [ 'type', CustomPalette ],
  contextPadProvider: [ 'type', CustomContextPad ],
  customRenderer: [ 'type', CustomRenderer ],
};
