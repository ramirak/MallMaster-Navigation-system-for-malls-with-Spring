//package twins.data;
//
//import java.util.Comparator;
//
//public class DistanceComparator implements Comparator<ItemEntity>{
//	private ItemEntity referenceItem;
//	
//		public DistanceComparator(ItemEntity referenceItem) {
//		    super();
//		    this.referenceItem = referenceItem;
//		}
//
//
//		@Override
//		public int compare(ItemEntity o1, ItemEntity o2) {
//		    return (calculateDistance(referenceItem, o1)<calculateDistance(referenceItem, o2)?-1:1);
//		}
//		
//
//}
